package com.joelzhu.bindview;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.joelzhu.bindview.annotations.FindView;
import com.joelzhu.bindview.annotations.OnClick;
import com.joelzhu.bindview.annotations.OnItemClick;
import com.joelzhu.bindview.annotations.RootView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import androidx.fragment.app.Fragment;

/**
 * Bind view through annotation.
 * This library only notice the annotation above the fields/methods, no matter what the
 * fields/methods' access modifiers are, such as, private or public, etc,.
 * Here're the examples:
 *
 * <b>1. SetContentView / LayoutInflater.inflate</b>
 * <code example>
 * {@literal @}RootView(R.layout.{resourceId})
 * class MainActivity extends Activity {}
 * </code>
 * <b>2. FindViewById</b>
 * <code example>
 * {@literal @}FindView(R.id.{resourceId})
 * TextView titleView;
 * </code>
 * <b>3. OnClickListener</b>
 * There're two cases allowed:
 * <code example1>
 * {@literal @}OnClick(R.id.{resourceId})
 * void onTextClicked() {}
 * </code>
 * <code example2>
 * {@literal @}OnClick(R.id.{resourceId})
 * void onTextClicked(View view) {}
 * </code>
 * <b>4. OnItemClickListener</b>
 * There're three cases allowed:
 * <code example1>
 * {@literal @}OnItemClick(R.id.{resourceId})
 * void onListItemClicked(int position) {}
 * </code>
 * <code example2>
 * {@literal @}OnItemClick(R.id.{resourceId})
 * void onListItemClicked(View view, int position) {}
 * </code>
 * <code example3>
 * {@literal @}OnItemClick(R.id.{resourceId})
 * void onListItemClicked(View view, int position, long id) {}
 * </code>
 */
public final class JZBindView {
    public static void bindView(Activity activity) {
        parseRootView(activity);
        parseFindView(activity);
        parseOnClick(activity);
        parseOnItemClick(activity);
    }
    
    public static View bindView(Fragment fragment, LayoutInflater inflater, ViewGroup container) {
        View rootView = parseRootView(fragment, inflater, container);
        parseFindView(fragment, rootView);
        parseOnClick(fragment, rootView);
        parseOnItemClick(fragment, rootView);
        return rootView;
    }
    
    /**
     * Parse the layout resource id from the annotation to call the setContentView(int).
     *
     * @param clazz The instance of Activity.
     */
    private static void parseRootView(Object clazz) {
        parseRootView(clazz, null, null);
    }
    
    /**
     * Parse the layout resource id from the annotation to call generate view of Fragment.
     *
     * @param clazz The instance of Activity/Fragment.
     * @param inflater Layout inflater.
     * @param parent Layout's parent.
     * @return Generated view.
     */
    private static View parseRootView(Object clazz, LayoutInflater inflater, ViewGroup parent) {
        if (clazz == null) {
            throw new RuntimeException("The instance of specified class is null.");
        }
        
        if (!(clazz instanceof Activity || clazz instanceof Fragment)) {
            throw new RuntimeException("Specified class is neither Activity nor Fragment.");
        }
        
        final RootView rootView = clazz.getClass().getAnnotation(RootView.class);
        if (rootView == null) {
            LogUtil.e("Write down '@RootView()' above the class definition.");
            throw new RuntimeException("Won't set root view.");
        }
        
        final int resourceId = rootView.value();
        if (resourceId == -1) {
            throw new RuntimeException("Didn't find the layout resource.");
        }
        
        if (clazz instanceof Activity) {
            // Current annotation belongs to Activity, call method setContentView.
            final Activity activity = (Activity) clazz;
            activity.setContentView(resourceId);
            return null;
        }
        
        // Current annotation belongs to Fragment, call inflate to generate root view.
        if (inflater == null) {
            throw new RuntimeException("Fragment's inflate is null.");
        }
        return inflater.inflate(resourceId, parent, false);
    }
    
    /**
     * Parse the view resource id from the annotation, then find the view instance from the
     * activity's content view, assign it to the field which was annotated.
     *
     * @param activity The instance of Activity.
     */
    private static void parseFindView(final Activity activity) {
        parseFindView(activity, activity);
    }
    
    /**
     * Parse the view resource id from the annotation, then find the view instance from the
     * fragment's content view, assign it to the field which was annotated.
     *
     * @param clazz The class which the field belongs to.
     * @param parent The parent.
     */
    private static void parseFindView(final Object clazz, final Object parent) {
        if (clazz == null) {
            throw new RuntimeException("The instance of specified class is null.");
        }
        
        if (!(clazz instanceof Activity || clazz instanceof Fragment)) {
            throw new RuntimeException("Specified class is neither Activity nor Fragment.");
        }
        
        final Field[] fields = clazz.getClass().getDeclaredFields();
        if (fields.length == 0) {
            final String className = clazz.getClass().getSimpleName();
            LogUtil.w(String.format("Get fields of class(%s) returns empty array.", className));
            return;
        }
        
        // Iterator the declared fields, assign the member field which is annotated by FindView.
        for (Field field : fields) {
            assignView(field, clazz, parent);
        }
    }
    
    /**
     * Parse the view resource id from the annotation, then find the view instance from the
     * activity's content view, invoke the annotated method when OnClick triggered.
     *
     * @param activity The instance of Activity.
     */
    private static void parseOnClick(final Activity activity) {
        parseOnClick(activity, activity);
    }
    
    /**
     * Parse the view resource id from the annotation, then find the view instance from the
     * activity's content view, invoke the annotated method when OnClick triggered.
     *
     * @param clazz The class which the field belongs to.
     * @param parent The parent.
     */
    private static void parseOnClick(final Object clazz, final Object parent) {
        if (clazz == null) {
            throw new RuntimeException("The instance of specified class is null.");
        }
        
        if (!(clazz instanceof Activity || clazz instanceof Fragment)) {
            throw new RuntimeException("Specified class is neither Activity nor Fragment.");
        }
        
        final Method[] methods = clazz.getClass().getDeclaredMethods();
        if (methods.length == 0) {
            LogUtil.w(String.format("Get methods of class(%s) returns empty array.",
                    clazz.getClass().getSimpleName()));
            return;
        }
        
        // Iterator the declared methods, register the OnClickListener to the corresponding view.
        for (final Method method : methods) {
            assignClick(method, clazz, parent);
        }
    }
    
    /**
     * Parse the view resource id from the annotation, then find the view instance from the
     * activity's content view, invoke the annotated method when OnClick triggered.
     *
     * @param activity The instance of Activity.
     */
    private static void parseOnItemClick(final Activity activity) {
        parseOnItemClick(activity, activity);
    }
    
    /**
     * Parse the view resource id from the annotation, then find the view instance from the
     * activity's content view, invoke the annotated method when OnClick triggered.
     *
     * @param clazz The class which the field belongs to.
     * @param parent The parent.
     */
    private static void parseOnItemClick(final Object clazz, final Object parent) {
        if (clazz == null) {
            throw new RuntimeException("The instance of specified class is null.");
        }
        
        if (!(clazz instanceof Activity || clazz instanceof Fragment)) {
            throw new RuntimeException("Specified class is neither Activity nor Fragment.");
        }
        
        final Method[] methods = clazz.getClass().getDeclaredMethods();
        if (methods.length == 0) {
            LogUtil.w(String.format("Get methods of class(%s) returns empty array.",
                    clazz.getClass().getSimpleName()));
            return;
        }
        
        // Iterator the declared methods, register the OnClickListener to the corresponding view.
        for (final Method method : methods) {
            assignItemClick(method, clazz, parent);
        }
    }
    
    /**
     * Assign the found out view to the field.
     *
     * @param field The annotated field.
     * @param clazz The class which the field belongs to.
     * @param parent The parent.
     */
    private static void assignView(final Field field, final Object clazz, final Object parent) {
        final FindView findView = field.getAnnotation(FindView.class);
        if (findView == null) {
            LogUtil.w("Didn't find '@FindView', ignore binding, field: " + field.getName());
            return;
        }
        
        try {
            int resourceId = findView.value();
            if (resourceId == -1) {
                throw new RuntimeException("Didn't find the specified view of this id.");
            }
            
            // Set accessible, which make us have the right to invoke the non-public method.
            field.setAccessible(true);
            field.set(clazz, findViewById(parent, resourceId));
        } catch (IllegalAccessException e) {
            LogUtil.e(e.getMessage());
            throw new RuntimeException("Something wrong when binding view.");
        }
    }
    
    /**
     * Assign the click event to the specified view.
     *
     * @param method Annotated method.
     * @param clazz The class which the method belongs to.
     * @param parent The parent view which the component lays on.
     */
    private static void assignClick(final Method method, final Object clazz, final Object parent) {
        final OnClick onClick = method.getAnnotation(OnClick.class);
        if (onClick == null) {
            LogUtil.w("Didn't find '@OnClick', ignore binding, method: " + method.getName());
            return;
        }
        
        int resourceId = onClick.value();
        if (resourceId == -1) {
            throw new RuntimeException("Didn't find the specified view of this id.");
        }
        
        // Register the OnClickListener to the view.
        final int resIdInnerClass = resourceId;
        final View view = findViewById(parent, resourceId);
        view.setTag(method);
        view.setOnClickListener(v -> {
            if (v.getId() == resIdInnerClass) {
                invokeClickMethod(clazz, v);
            }
        });
    }
    
    /**
     * Assign the click event to the specified view.
     *
     * @param method Annotated method.
     * @param clazz The class which the method belongs to.
     * @param parent The parent view which the component lays on.
     */
    private static void assignItemClick(final Method method, final Object clazz,
            final Object parent) {
        final OnItemClick onItemClick = method.getAnnotation(OnItemClick.class);
        if (onItemClick == null) {
            LogUtil.w("Didn't find '@OnItemClick', ignore binding, method: " + method.getName());
            return;
        }
        
        int resourceId = onItemClick.value();
        if (resourceId == -1) {
            throw new RuntimeException("Didn't find the specified view of this id.");
        }
        
        // Register the OnItemClickListener to the ListView.
        final int resIdInnerClass = resourceId;
        final View view = findViewById(parent, resourceId);
        if (!(view instanceof ListView)) {
            throw new RuntimeException("The binding view has no item click event, view: " +
                    view.getClass().getSimpleName());
        }
        final ListView listView = (ListView) view;
        listView.setTag(method);
        listView.setOnItemClickListener((parent1, view1, position, id) -> {
            if (parent1.getId() == resIdInnerClass) {
                invokeItemClickMethod(clazz, parent1, view1, position, id);
            }
        });
    }
    
    /**
     * Find the component in specified view.
     *
     * @param parent Specified view, pass activity or root view.
     * @param resId Resource id of component.
     * @return Matched component found out by resource id.
     */
    private static View findViewById(final Object parent, final int resId) {
        // In Activity.
        if (parent instanceof Activity) {
            return ((Activity) parent).findViewById(resId);
        }
        // In Fragment.
        else if (parent instanceof View) {
            return ((View) parent).findViewById(resId);
        }
        // Wrong case.
        else {
            throw new RuntimeException("RootView is neither View nor Activity.");
        }
    }
    
    /**
     * Invoke the method.
     * Due to the annotation allows user to annotated at the method which has the only parameter of
     * {@link View}, so, we need to pass the parameter when calling the method.
     *
     * @param clazz The class which the method belongs to.
     * @param view The view which being clicked.
     */
    private static void invokeClickMethod(final Object clazz, final View view) {
        try {
            final Method method = (Method) view.getTag();
            final Type[] types = method.getGenericParameterTypes();
            // Annotated the method with non-parameter.
            if (types.length == 0) {
                // Set accessible, which make us have the right to invoke the non-public method.
                method.setAccessible(true);
                method.invoke(clazz);
            }
            // Annotated the method with one parameter, the clicked view.
            else if (types.length == 1 && types[0] == View.class) {
                // Set accessible, which make us have the right to invoke the non-public method.
                method.setAccessible(true);
                method.invoke(clazz, view);
            }
            // The parameters unmatched, throw runtime exception.
            else {
                throw new RuntimeException("Unmatched parameter of OnClick method.");
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            LogUtil.e(e.getMessage());
            throw new RuntimeException("Something wrong when binding view.");
        }
    }
    
    private static void invokeItemClickMethod(final Object clazz, AdapterView<?> parent, View view,
            int position, long id) {
        try {
            final Method method = (Method) parent.getTag();
            final Type[] types = method.getGenericParameterTypes();
            // Annotated the method with one parameter, clicked item's position.
            if (types.length == 1 && types[0] == int.class) {
                // Set accessible, which make us have the right to invoke the non-public method.
                method.setAccessible(true);
                method.invoke(clazz, position);
            }
            // Annotated the method with two parameters, clicked item's view and position.
            else if (types.length == 2 && types[0] == View.class && types[1] == int.class) {
                // Set accessible, which make us have the right to invoke the non-public method.
                method.setAccessible(true);
                method.invoke(clazz, view, position);
            }
            // Annotated the method with three parameters, the same as the three parameters at the
            // tail of the onItemClick in Android.
            else if (types.length == 3 &&
                    types[0] == View.class &&
                    types[1] == int.class &&
                    types[2] == long.class) {
                // Set accessible, which make us have the right to invoke the non-public method.
                method.setAccessible(true);
                method.invoke(clazz, view, position, id);
            }
            // The parameters unmatched, throw runtime exception.
            else {
                throw new RuntimeException("Unmatched parameter of OnItemClick method.");
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            LogUtil.e(e.getMessage());
            throw new RuntimeException("Something wrong when binding view.");
        }
    }
}