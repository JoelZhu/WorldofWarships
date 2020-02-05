package com.joelzhu.bindview;

import android.app.Activity;
import android.view.View;

import com.joelzhu.bindview.annotations.ContentView;
import com.joelzhu.bindview.annotations.FindView;
import com.joelzhu.bindview.annotations.OnClick;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public final class JZBindView {
    public static void bindView(final Activity activity) {
        parseContentView(activity);
        parseFindView(activity);
        parseOnClick(activity);
    }

    /**
     * Parse the layout resource id from the annotation to call the setContentView(int).
     *
     * @param activity The instance of Activity.
     */
    private static void parseContentView(final Activity activity) {
        if (activity == null) {
            throw new RuntimeException("The instance of activity is null.");
        }

        final ContentView contentView = activity.getClass().getAnnotation(ContentView.class);
        if (contentView == null) {
            LogUtil.e("Write down '@ContentView()' above the class definition.");
            throw new RuntimeException("Didn't set content view.");
        }

        activity.setContentView(contentView.value());
    }

    /**
     * Parse the view resource id from the annotation, then find the view instance from the
     * activity's content view, assign it to the field which was annotated.
     *
     * @param activity The instance of Activity.
     */
    private static void parseFindView(final Activity activity) {
        if (activity == null) {
            throw new RuntimeException("The instance of activity is null.");
        }

        final Field[] fields = activity.getClass().getDeclaredFields();
        if (fields.length == 0) {
            LogUtil.w(String.format("Get fields of class(%s) returns empty array.",
                    activity.getClass().getSimpleName()));
            return;
        }

        // Iterator the declared fields, assign the member field which is annotated by FindView.
        for (Field field : fields) {
            FindView findView = field.getAnnotation(FindView.class);
            if (findView == null) {
                LogUtil.w("Didn't find 'FindView' annotation, ignore binding FindView.");
                continue;
            }

            try {
                // Set accessible, which make us have the right to invoke the
                // non-public method.
                field.setAccessible(true);
                // Assign the member field with the found view.
                field.set(activity, activity.findViewById(findView.value()));
            } catch (IllegalAccessException e) {
                LogUtil.e(e.getMessage());
                throw new RuntimeException("Something wrong when binding view.");
            }
        }
    }

    /**
     * Parse the view resource id from the annotation, then find the view instance from the
     * activity's content view, invoke the annotated method when OnClick triggered.
     *
     * @param activity The instance of Activity.
     */
    private static void parseOnClick(final Activity activity) {
        if (activity == null) {
            throw new RuntimeException("The instance of activity is null.");
        }

        final Method[] methods = activity.getClass().getDeclaredMethods();
        if (methods.length == 0) {
            LogUtil.w(String.format("Get methods of class(%s) returns empty array.",
                    activity.getClass().getSimpleName()));
            return;
        }

        // Iterator the declared methods, register the OnClickListener to the corresponding view.
        for (final Method method : methods) {
            final OnClick onClick = method.getAnnotation(OnClick.class);
            if (onClick == null) {
                LogUtil.w("Didn't find 'OnClick' annotation, ignore binding OnClick.");
                continue;
            }

            final View view = activity.findViewById(onClick.value());
            if (view == null) {
                throw new RuntimeException("Didn't find the view of id: " + onClick.value());
            }

            // Register the OnClickListener to the view.
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == onClick.value()) {
                        try {
                            final Type[] types = method.getGenericParameterTypes();
                            // If annotated non-parameter method.
                            if (types.length == 0) {
                                // Set accessible, which make us have the right to invoke the
                                // non-public method.
                                method.setAccessible(true);
                                method.invoke(activity);
                            }
                            // If annotated method with one parameter of View.class.
                            else if (types.length == 1 && types[0] == View.class) {
                                // Set accessible, which make us have the right to invoke the
                                // non-public method.
                                method.setAccessible(true);
                                // Invoke the method with the instance of View.class.
                                method.invoke(activity, v);
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
            });
        }
    }
}