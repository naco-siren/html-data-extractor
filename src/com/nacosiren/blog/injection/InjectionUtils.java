package com.nacosiren.blog.injection;

/**
 * Created by nacos on 4/26/2017.
 */
public class InjectionUtils {
    /* Dimensions */
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String AREA = "area";

    /* Margins and paddings */
    public static final String MARGIN_LEFT = "margin-left";
    public static final String MARGIN_RIGHT = "margin-right";
    public static final String PADDING_LEFT = "padding-left";
    public static final String PADDING_RIGHT = "padding-right";

    /* Font */
    public static final String FONT_FAMILY = "font-family";
    public static final String FONT_STYLE = "font-style";
    public static final String FONT_SIZE = "font-size";
    public static final String FONT_WEIGHT = "font-weight";
    public static final String FONT_VARIANT = "font-variant";


    /**
     * Parse the integer value from the attribute value String
     * @param px
     * @return The value without units (px)
     */
    public static int px2int(String px) {
        if (px == null)
            return 0;

        String[] parts = px.split("px");
        if (parts.length == 0)
            return 0;

        try {
            int value = Integer.parseInt(parts[0]);
            return value;

        } catch (Exception e) {
            return 0;
        }
    }
}
