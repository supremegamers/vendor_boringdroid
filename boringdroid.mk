# Recents
PRODUCT_SYSTEM_DEFAULT_PROPERTIES += \
    ro.recents.grid=true \
    persist.sys.pcmode.enabled=true \
    persist.sys.systemuiplugin.enabled=true \

PRODUCT_PACKAGES := \
    BoringdroidSystemUIApk \
    BoringdroidSettingsApk \

# rro overlay
PRODUCT_PACKAGES += \
    BoringdroidSystemUIOverlay

PRODUCT_COPY_FILES := \
    frameworks/native/data/etc/android.software.freeform_window_management.xml:system/etc/permissions/android.software.freeform_window_management.xml

