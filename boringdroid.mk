# Recents
PRODUCT_SYSTEM_DEFAULT_PROPERTIES += \
    ro.recents.grid=true \
    persist.sys.pcmode.enabled=true \
    persist.sys.systemuiplugin.enabled=true \
    persist.sys.settings.tunerkeys=sysui_nav_bar--sysui_nav_bar_left--sysui_nav_bar_right\

PRODUCT_PACKAGES := \
    BoringdroidSystemUI \
    BoringdroidSettings

PRODUCT_COPY_FILES := \
    frameworks/native/data/etc/android.software.freeform_window_management.xml:system/etc/permissions/android.software.freeform_window_management.xml

