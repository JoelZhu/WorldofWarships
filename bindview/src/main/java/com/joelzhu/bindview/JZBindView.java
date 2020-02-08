package com.joelzhu.bindview;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.joelzhu.bindview.annotations.FindView;
import com.joelzhu.bindview.annotations.OnClick;
import com.joelzhu.bindview.annotations.RootView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public final class JZBindView {
    public static void bindView(Activity activity) {
        parseRootView(activity);
        parseFindView(activity);
        parseOnClick(activity);
    }

    public static View bindView(Fragment fragment, LayoutInflater inflater, ViewGroup container) {
        View rootView = parseRootView(fragment, inflater, container);
        parseFindView(fragment, rootView);
        parseOnClick(fragment, rootView);
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
     * @param clazz    The instance of Activity/Fragment.
     * @param inflater Layout inflater.
     * @param parent   Layout's parent.
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

        int resourceId = rootView.resId();
        if (resourceId == -1) {
            // In Android Library, won't generate const resource id in R.java, use resName instead.
            final Context context = clazz instanceof Activity ?
                    ((Activity) clazz) :
                    ((Fragment) clazz).getContext();
            resourceId = getLayoutResource(context, rootView.resName());
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
     * @param clazz  The class which the field belongs to.
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
     * @param clazz  The class which the field belongs to.
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
     * Assign the found out view to the field.
     *
     * @param field  The annotated field.
     * @param clazz  The class which the field belongs to.
     * @param parent The parent.
     */
    private static void assignView(final Field field, final Object clazz, final Object parent) {
        final FindView findView = field.getAnnotation(FindView.class);
        if (findView == null) {
            LogUtil.w("Didn't find '@FindView', ignore binding, field: " + field.getName());
            return;
        }

        try {
            int resourceId = findView.resId();
            if (resourceId == -1) {
                // In Android Library, won't generate const resource id in R.java, use resName instead.
                final Context context = clazz instanceof Activity ?
                        ((Activity) clazz) :
                        ((Fragment) clazz).getContext();
                resourceId = getIdResource(context, findView.resName());
            }

            if (resourceId == 0) {
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
     * @param clazz  The class which the method belongs to.
     * @param parent The parent view which the component lays on.
     */
    private static void assignClick(final Method method, final Object clazz, final Object parent) {
        final OnClick onClick = method.getAnnotation(OnClick.class);
        if (onClick == null) {
            LogUtil.w("Didn't find '@OnClick', ignore binding, method: " + method.getName());
            return;
        }

        int resourceId = onClick.resId();
        if (resourceId == -1) {
            // In Android Library, won't generate const resource id in R.java, use resName instead.
            final Context context = clazz instanceof Activity ?
                    ((Activity) clazz) :
                    ((Fragment) clazz).getContext();
            resourceId = getIdResource(context, onClick.resName());
        }

        if (resourceId == 0) {
            throw new RuntimeException("Didn't find the specified view of this id.");
        }

        // Register the OnClickListener to the view.
        final int resIdInnerClass = resourceId;
        final View view = findViewById(parent, resourceId);
        view.setTag(method);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == resIdInnerClass) {
                    invokeCorrespondingMethod(clazz, v);
                }
            }
        });
    }

    /**
     * Find the component in specified view.
     *
     * @param parent Specified view, pass activity or root view.
     * @param resId  Resource id of component.
     * @return Matched component found out by resource id.
     */
    private static View findViewById(final Object parent, final int resId) {
        if (parent instanceof Activity) {
            // In Activity.
            return ((Activity) parent).findViewById(resId);
        } else if (parent instanceof View) {
            // In Fragment.
            return ((View) parent).findViewById(resId);
        } else {
            throw new RuntimeException("RootView is neither View nor Activity.");
        }
    }

    /**
     * Find out the layout resource id through reflex by resource name.
     *
     * @param context       Android context.
     * @param layoutResName Resource name.
     * @return Resource id.
     */
    private static int getLayoutResource(Context context, String layoutResName) {
        if (context == null) {
            throw new RuntimeException("Context is null.");
        }

        final Resources resources = context.getResources();
        if (resources == null) {
            throw new RuntimeException("Didn't get resources instance.");
        }
        return resources.getIdentifier(layoutResName, "layout", context.getPackageName());
    }

    /**
     * Find out the component resource id through reflex by resource name.
     *
     * @param context   Android context.
     * @param idResName Resource name.
     * @return Resource id.
     */
    private static int getIdResource(Context context, String idResName) {
        if (context == null) {
            throw new RuntimeException("Context is null.");
        }

        final Resources resources = context.getResources();
        if (resources == null) {
            throw new RuntimeException("Didn't get resources instance.");
        }
        return resources.getIdentifier(idResName, "id", context.getPackageName());
    }

    /**
     * Invoke the method.
     * Due to the annotation allows user to annotated at the method which has the only parameter of
     * {@link View}, so, we need to pass the parameter when calling the method.
     *
     * @param clazz The class which the method belongs to.
     * @param view  The view which being clicked.
     */
    private static void invokeCorrespondingMethod(final Object clazz, final View view) {
        try {
            final Method method = (Method) view.getTag();
            final Type[] types = method.getGenericParameterTypes();
            // If annotated non-parameter method.
            if (types.length == 0) {
                // Set accessible, which make us have the right to invoke the
                // non-public method.
                method.setAccessible(true);
                method.invoke(clazz);
            }
            // If annotated method with one parameter of View.class.
            else if (types.length == 1 && types[0] == View.class) {
                // Set accessible, which make us have the right to invoke the
                // non-public method.
                method.setAccessible(true);
                // Invoke the method with the instance of View.class.
                method.invoke(clazz, view);
            }
            // The parameter unmatched, throw the exception.
            else {
                throw new RuntimeException("Unmatched parameter of OnClick method.");
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            LogUtil.e(e.getMessage());
            throw new RuntimeException("Something wrong when binding view.");
        }
    }
}