# Recents
PRODUCT_SYSTEM_DEFAULT_PROPERTIES += \
    ro.recents.grid=true \
    persist.sys.systemuiplugin.enabled=true

PRODUCT_PACKAGES := \
    BoringdroidSystemUI

PRODUCT_COPY_FILES := \
    frameworks/native/data/etc/android.software.freeform_window_management.xml:system/etc/permissions/android.software.freeform_window_management.xml

