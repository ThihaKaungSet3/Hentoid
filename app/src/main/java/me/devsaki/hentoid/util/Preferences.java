package me.devsaki.hentoid.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.annimon.stream.Stream;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import me.devsaki.hentoid.BuildConfig;
import me.devsaki.hentoid.enums.Site;
import timber.log.Timber;

import static android.os.Build.VERSION_CODES.P;

/**
 * Created by Shiro on 2/21/2018.
 * Decorator class that wraps a SharedPreference to implement properties
 * Some properties do not have a setter because it is changed by PreferenceActivity
 * Some properties are parsed as ints because of limitations with the Preference subclass used
 */

public final class Preferences {

    private Preferences() {
        throw new IllegalStateException("Utility class");
    }

    private static final int VERSION = 4;

    private static SharedPreferences sharedPreferences;

    public static void init(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        int savedVersion = sharedPreferences.getInt(Key.PREFS_VERSION_KEY, VERSION);
        if (savedVersion != VERSION) {
            Timber.d("Shared Prefs Key Mismatch! Clearing Prefs!");
            sharedPreferences.edit()
                    .clear()
                    .apply();
        }
    }

    public static void performHousekeeping() {
        // Fling factor -> Swipe to fling (v1.9.0)
        if (sharedPreferences.contains(Key.PREF_VIEWER_FLING_FACTOR)) {
            int flingFactor = Integer.parseInt(sharedPreferences.getString(Key.PREF_VIEWER_FLING_FACTOR, "0") + "");
            sharedPreferences.edit().putBoolean(Key.PREF_VIEWER_SWIPE_TO_FLING, flingFactor > 0).apply();
            sharedPreferences.edit().remove(Key.PREF_VIEWER_FLING_FACTOR).apply();
        }

        if (sharedPreferences.contains(Key.PREF_ANALYTICS_TRACKING)) {
            boolean analyticsTracking = sharedPreferences.getBoolean(Key.PREF_ANALYTICS_TRACKING, false);
            sharedPreferences.edit().putBoolean(Key.PREF_ANALYTICS_PREFERENCE, !analyticsTracking).apply();
            sharedPreferences.edit().remove(Key.PREF_ANALYTICS_TRACKING).apply();
        }

        if (sharedPreferences.contains(Key.PREF_HIDE_RECENT)) {
            boolean hideRecent = sharedPreferences.getBoolean(Key.PREF_HIDE_RECENT, !BuildConfig.DEBUG);
            sharedPreferences.edit().putBoolean(Key.PREF_APP_PREVIEW, !hideRecent).apply();
            sharedPreferences.edit().remove(Key.PREF_HIDE_RECENT).apply();
        }

        if (sharedPreferences.contains(Key.PREF_CHECK_UPDATES_LISTS)) {
            boolean checkUpdates = sharedPreferences.getBoolean(Key.PREF_CHECK_UPDATES_LISTS, Default.PREF_CHECK_UPDATES_DEFAULT);
            sharedPreferences.edit().putBoolean(Key.PREF_CHECK_UPDATES, checkUpdates).apply();
            sharedPreferences.edit().remove(Key.PREF_CHECK_UPDATES_LISTS).apply();
        }
    }

    public static void registerPrefsChangedListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unregisterPrefsChangedListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public static boolean isFirstRunProcessComplete() {
        return sharedPreferences.getBoolean(Key.PREF_WELCOME_DONE, false);
    }

    public static void setIsFirstRunProcessComplete(boolean isFirstRunProcessComplete) {
        sharedPreferences.edit()
                .putBoolean(Key.PREF_WELCOME_DONE, isFirstRunProcessComplete)
                .apply();
    }

    public static boolean isAnalyticsEnabled() {
        return sharedPreferences.getBoolean(Key.PREF_ANALYTICS_PREFERENCE, true);
    }

    public static boolean isAutomaticUpdateEnabled() {
        return sharedPreferences.getBoolean(Key.PREF_CHECK_UPDATES, Default.PREF_CHECK_UPDATES_DEFAULT);
    }

    public static boolean isFirstRun() {
        return sharedPreferences.getBoolean(Key.PREF_FIRST_RUN, Default.PREF_FIRST_RUN_DEFAULT);
    }

    public static void setIsFirstRun(boolean isFirstRun) {
        sharedPreferences.edit()
                .putBoolean(Key.PREF_FIRST_RUN, isFirstRun)
                .apply();
    }

    public static int getContentSortOrder() {
        return sharedPreferences.getInt(Key.PREF_ORDER_CONTENT_LISTS, Default.PREF_ORDER_CONTENT_DEFAULT);
    }

    public static void setContentSortOrder(int sortOrder) {
        sharedPreferences.edit()
                .putInt(Key.PREF_ORDER_CONTENT_LISTS, sortOrder)
                .apply();
    }

    public static int getAttributesSortOrder() {
        return Integer.parseInt(sharedPreferences.getString(Key.PREF_ORDER_ATTRIBUTE_LISTS, Default.PREF_ORDER_ATTRIBUTES_DEFAULT + "") + "");
    }

    public static int getContentPageQuantity() {
        return Integer.parseInt(sharedPreferences.getString(Key.PREF_QUANTITY_PER_PAGE_LISTS,
                Default.PREF_QUANTITY_PER_PAGE_DEFAULT + "") + "");
    }

    public static String getAppLockPin() {
        return sharedPreferences.getString(Key.PREF_APP_LOCK, "");
    }

    public static void setAppLockPin(String pin) {
        sharedPreferences.edit()
                .putString(Key.PREF_APP_LOCK, pin)
                .apply();
    }

    public static boolean getEndlessScroll() {
        return sharedPreferences.getBoolean(Key.PREF_ENDLESS_SCROLL, Default.PREF_ENDLESS_SCROLL_DEFAULT);
    }

    public static boolean getRecentVisibility() {
        return sharedPreferences.getBoolean(Key.PREF_APP_PREVIEW, BuildConfig.DEBUG);
    }

    public static String getSdStorageUri() {
        return sharedPreferences.getString(Key.PREF_SD_STORAGE_URI, "");
    }

    static void setSdStorageUri(String uri) {
        sharedPreferences.edit()
                .putString(Key.PREF_SD_STORAGE_URI, uri)
                .apply();
    }

    static int getFolderNameFormat() {
        return Integer.parseInt(
                sharedPreferences.getString(Key.PREF_FOLDER_NAMING_CONTENT_LISTS,
                        Default.PREF_FOLDER_NAMING_CONTENT_DEFAULT + "") + "");
    }

    public static String getRootFolderName() {
        return sharedPreferences.getString(Key.PREF_SETTINGS_FOLDER, "");
    }

    static boolean setRootFolderName(String rootFolderName) {
        return sharedPreferences.edit()
                .putString(Key.PREF_SETTINGS_FOLDER, rootFolderName)
                .commit();
    }

    public static int getWebViewInitialZoom() {
        return Integer.parseInt(
                sharedPreferences.getString(
                        Key.PREF_WEBVIEW_INITIAL_ZOOM_LISTS,
                        Default.PREF_WEBVIEW_INITIAL_ZOOM_DEFAULT + "") + "");
    }

    public static boolean getWebViewOverview() {
        return sharedPreferences.getBoolean(
                Key.PREF_WEBVIEW_OVERRIDE_OVERVIEW_LISTS,
                Default.PREF_WEBVIEW_OVERRIDE_OVERVIEW_DEFAULT);
    }

    public static boolean isBrowserResumeLast() {
        return sharedPreferences.getBoolean(Key.PREF_BROWSER_RESUME_LAST, Default.PREF_BROWSER_RESUME_LAST_DEFAULT);
    }

    public static boolean isBrowserAugmented() {
        return sharedPreferences.getBoolean(Key.PREF_BROWSER_AUGMENTED, Default.PREF_BROWSER_AUGMENTED_DEFAULT);
    }

    public static int getDownloadThreadCount() {
        return Integer.parseInt(sharedPreferences.getString(Key.PREF_DL_THREADS_QUANTITY_LISTS,
                Default.PREF_DL_THREADS_QUANTITY_DEFAULT + "") + "");
    }

    static int getFolderTruncationNbChars() {
        return Integer.parseInt(sharedPreferences.getString(Key.PREF_FOLDER_TRUNCATION_LISTS,
                Default.PREF_FOLDER_TRUNCATION_DEFAULT + "") + "");
    }

    public static boolean isViewerResumeLastLeft() {
        return sharedPreferences.getBoolean(Key.PREF_VIEWER_RESUME_LAST_LEFT, Default.PREF_VIEWER_RESUME_LAST_LEFT);
    }

    public static void setViewerResumeLastLeft(boolean resumeLastLeft) {
        sharedPreferences.edit()
                .putBoolean(Key.PREF_VIEWER_RESUME_LAST_LEFT, resumeLastLeft)
                .apply();
    }

    public static boolean isViewerKeepScreenOn() {
        return sharedPreferences.getBoolean(Key.PREF_VIEWER_KEEP_SCREEN_ON, Default.PREF_VIEWER_KEEP_SCREEN_ON);
    }

    public static void setViewerKeepScreenOn(boolean keepScreenOn) {
        sharedPreferences.edit()
                .putBoolean(Key.PREF_VIEWER_KEEP_SCREEN_ON, keepScreenOn)
                .apply();
    }

    public static int getViewerResizeMode() {
        return Integer.parseInt(sharedPreferences.getString(Key.PREF_VIEWER_IMAGE_DISPLAY, Integer.toString(Default.PREF_VIEWER_IMAGE_DISPLAY)) + "");
    }

    public static void setViewerResizeMode(int resizeMode) {
        sharedPreferences.edit()
                .putString(Key.PREF_VIEWER_IMAGE_DISPLAY, Integer.toString(resizeMode))
                .apply();
    }

    public static int getViewerBrowseMode() {
        return Integer.parseInt(sharedPreferences.getString(Key.PREF_VIEWER_BROWSE_MODE, Integer.toString(Default.PREF_VIEWER_BROWSE_MODE)) + "");
    }

    public static int getViewerDirection() {
        return (getViewerBrowseMode() == Constant.PREF_VIEWER_BROWSE_RTL) ? Constant.PREF_VIEWER_DIRECTION_RTL : Constant.PREF_VIEWER_DIRECTION_LTR;
    }

    public static int getViewerOrientation() {
        return (getViewerBrowseMode() == Constant.PREF_VIEWER_BROWSE_TTB) ? Constant.PREF_VIEWER_ORIENTATION_VERTICAL : Constant.PREF_VIEWER_ORIENTATION_HORIZONTAL;
    }

    public static void setViewerBrowseMode(int browseMode) {
        sharedPreferences.edit()
                .putString(Key.PREF_VIEWER_BROWSE_MODE, Integer.toString(browseMode))
                .apply();
    }

    public static boolean isViewerDisplayPageNum() {
        return sharedPreferences.getBoolean(Key.PREF_VIEWER_DISPLAY_PAGENUM, Default.PREF_VIEWER_DISPLAY_PAGENUM);
    }

    public static void setViewerDisplayPageNum(boolean displayPageNum) {
        sharedPreferences.edit()
                .putBoolean(Key.PREF_VIEWER_DISPLAY_PAGENUM, displayPageNum)
                .apply();
    }

    public static boolean isViewerTapTransitions() {
        return sharedPreferences.getBoolean(Key.PREF_VIEWER_TAP_TRANSITIONS, Default.PREF_VIEWER_TAP_TRANSITIONS);
    }

    public static void setViewerTapTransitions(boolean tapTransitions) {
        sharedPreferences.edit()
                .putBoolean(Key.PREF_VIEWER_TAP_TRANSITIONS, tapTransitions)
                .apply();
    }

    public static boolean isViewerSwipeToFling() {
        return sharedPreferences.getBoolean(Key.PREF_VIEWER_SWIPE_TO_FLING, Default.PREF_VIEWER_SWIPE_TO_FLING);
    }

    public static void setViewerSwipeToFling(boolean swipeToFling) {
        sharedPreferences.edit()
                .putBoolean(Key.PREF_VIEWER_SWIPE_TO_FLING, swipeToFling)
                .apply();
    }

    public static boolean isViewerInvertVolumeRocker() {
        return sharedPreferences.getBoolean(Key.PREF_VIEWER_INVERT_VOLUME_ROCKER, Default.PREF_VIEWER_INVERT_VOLUME_ROCKER);
    }

    public static void setViewerInvertVolumeRocker(boolean invertVolumeRocker) {
        sharedPreferences.edit()
                .putBoolean(Key.PREF_VIEWER_INVERT_VOLUME_ROCKER, invertVolumeRocker)
                .apply();
    }

    public static boolean isOpenBookInGalleryMode() {
        return sharedPreferences.getBoolean(Key.PREF_VIEWER_OPEN_GALLERY, Default.PREF_VIEWER_OPEN_GALLERY);
    }

    public static void setOpenBookInGalleryMode(boolean openBookInGalleryMode) {
        sharedPreferences.edit()
                .putBoolean(Key.PREF_VIEWER_OPEN_GALLERY, openBookInGalleryMode)
                .apply();
    }

    public static int getLastKnownAppVersionCode() {
        return Integer.parseInt(sharedPreferences.getString(Key.LAST_KNOWN_APP_VERSION_CODE, "0") + "");
    }

    public static void setLastKnownAppVersionCode(int versionCode) {
        sharedPreferences.edit()
                .putString(Key.LAST_KNOWN_APP_VERSION_CODE, Integer.toString(versionCode))
                .apply();
    }

    public static int getDarkMode() {
        return Integer.parseInt(sharedPreferences.getString(Key.DARK_MODE, Integer.toString(Default.PREF_DARK_MODE)) + "");
    }

    public static void setDarkMode(int darkMode) {
        sharedPreferences.edit()
                .putString(Key.DARK_MODE, Integer.toString(darkMode))
                .apply();
    }

    public static boolean isDlRetriesActive() {
        return sharedPreferences.getBoolean(Key.PREF_DL_RETRIES_ACTIVE, Default.PREF_DL_RETRIES_ACTIVE);
    }

    public static int getDlRetriesNumber() {
        return Integer.parseInt(sharedPreferences.getString(Key.PREF_DL_RETRIES_NUMBER, Integer.toString(Default.PREF_DL_RETRIES_NUMBER)) + "");
    }

    public static int getDlRetriesMemLimit() {
        return Integer.parseInt(sharedPreferences.getString(Key.PREF_DL_RETRIES_MEM_LIMIT, Integer.toString(Default.PREF_DL_RETRIES_MEM_LIMIT)) + "");
    }

    public static boolean isDlHitomiWebp() {
        return sharedPreferences.getBoolean(Key.PREF_DL_HITOMI_WEBP, Default.PREF_DL_HITOMI_WEBP);
    }

    public static List<Site> getActiveSites() {
        String siteCodesStr = sharedPreferences.getString(Key.ACTIVE_SITES, Default.ACTIVE_SITES) + "";
        if (siteCodesStr.isEmpty()) return Collections.emptyList();

        List<String> siteCodes = Arrays.asList(siteCodesStr.split(","));
        return Stream.of(siteCodes).map(s -> Site.searchByCode(Long.valueOf(s))).toList();
    }

    public static void setActiveSites(List<Site> activeSites) {
        List<Integer> siteCodes = Stream.of(activeSites).map(Site::getCode).toList();
        sharedPreferences.edit()
                .putString(Key.ACTIVE_SITES, android.text.TextUtils.join(",", siteCodes))
                .apply();
    }

    public static final class Key {

        private Key() {
            throw new IllegalStateException("Utility class");
        }

        public static final String PREF_ANALYTICS_PREFERENCE = "pref_analytics_preference";
        public static final String PREF_APP_LOCK = "pref_app_lock";
        public static final String PREF_APP_PREVIEW = "pref_app_preview";
        public static final String PREF_ADD_NO_MEDIA_FILE = "pref_add_no_media_file";
        static final String PREF_CHECK_UPDATES = "pref_check_updates";
        public static final String PREF_CHECK_UPDATE_MANUAL = "pref_check_updates_manual";
        public static final String PREF_REFRESH_LIBRARY = "pref_refresh_bookshelf";
        static final String PREF_WELCOME_DONE = "pref_welcome_done";
        static final String PREFS_VERSION_KEY = "prefs_version";
        static final String PREF_QUANTITY_PER_PAGE_LISTS = "pref_quantity_per_page_lists";
        static final String PREF_ORDER_CONTENT_LISTS = "pref_order_content_lists";
        static final String PREF_ORDER_ATTRIBUTE_LISTS = "pref_order_attribute_lists";
        static final String PREF_FIRST_RUN = "pref_first_run";
        public static final String PREF_ENDLESS_SCROLL = "pref_endless_scroll";
        static final String PREF_SD_STORAGE_URI = "pref_sd_storage_uri";
        static final String PREF_FOLDER_NAMING_CONTENT_LISTS = "pref_folder_naming_content_lists";
        static final String PREF_SETTINGS_FOLDER = "folder";
        static final String PREF_WEBVIEW_OVERRIDE_OVERVIEW_LISTS = "pref_webview_override_overview_lists";
        static final String PREF_WEBVIEW_INITIAL_ZOOM_LISTS = "pref_webview_initial_zoom_lists";
        static final String PREF_BROWSER_RESUME_LAST = "pref_browser_resume_last";
        static final String PREF_BROWSER_AUGMENTED = "pref_browser_augmented";
        static final String PREF_FOLDER_TRUNCATION_LISTS = "pref_folder_trunc_lists";
        static final String PREF_VIEWER_RESUME_LAST_LEFT = "pref_viewer_resume_last_left";
        public static final String PREF_VIEWER_KEEP_SCREEN_ON = "pref_viewer_keep_screen_on";
        public static final String PREF_VIEWER_IMAGE_DISPLAY = "pref_viewer_image_display";
        public static final String PREF_VIEWER_BROWSE_MODE = "pref_viewer_browse_mode";
        public static final String PREF_VIEWER_DISPLAY_PAGENUM = "pref_viewer_display_pagenum";
        public static final String PREF_VIEWER_SWIPE_TO_FLING = "pref_viewer_swipe_to_fling";
        static final String PREF_VIEWER_TAP_TRANSITIONS = "pref_viewer_tap_transitions";
        static final String PREF_VIEWER_OPEN_GALLERY = "pref_viewer_open_gallery";
        public static final String PREF_VIEWER_INVERT_VOLUME_ROCKER = "pref_viewer_invert_volume_rocker";
        static final String LAST_KNOWN_APP_VERSION_CODE = "last_known_app_version_code";
        public static final String DARK_MODE = "pref_dark_mode";
        static final String PREF_DL_RETRIES_ACTIVE = "pref_dl_retries_active";
        static final String PREF_DL_RETRIES_NUMBER = "pref_dl_retries_number";
        static final String PREF_DL_RETRIES_MEM_LIMIT = "pref_dl_retries_mem_limit";
        static final String PREF_DL_HITOMI_WEBP = "pref_dl_hitomi_webp";
        public static final String PREF_DL_THREADS_QUANTITY_LISTS = "pref_dl_threads_quantity_lists";
        public static final String ACTIVE_SITES = "active_sites";

        //Keys that were removed from the app, kept for housekeeping
        static final String PREF_ANALYTICS_TRACKING = "pref_analytics_tracking";
        static final String PREF_HIDE_RECENT = "pref_hide_recent";
        static final String PREF_VIEWER_FLING_FACTOR = "pref_viewer_fling_factor";
        static final String PREF_CHECK_UPDATES_LISTS = "pref_check_updates_lists";
    }

    // IMPORTANT : Any default value change must be mirrored in res/values/strings_settings.xml
    public static final class Default {

        private Default() {
            throw new IllegalStateException("Utility class");
        }

        static final int PREF_QUANTITY_PER_PAGE_DEFAULT = 20;
        static final int PREF_ORDER_CONTENT_DEFAULT = Constant.ORDER_CONTENT_TITLE_ALPHA;
        static final int PREF_ORDER_ATTRIBUTES_DEFAULT = Constant.ORDER_ATTRIBUTES_COUNT;
        static final boolean PREF_FIRST_RUN_DEFAULT = true;
        static final boolean PREF_ENDLESS_SCROLL_DEFAULT = true;
        static final int PREF_FOLDER_NAMING_CONTENT_DEFAULT = Constant.PREF_FOLDER_NAMING_CONTENT_AUTH_TITLE_ID;
        static final boolean PREF_WEBVIEW_OVERRIDE_OVERVIEW_DEFAULT = false;
        public static final int PREF_WEBVIEW_INITIAL_ZOOM_DEFAULT = 20;
        static final boolean PREF_BROWSER_RESUME_LAST_DEFAULT = false;
        static final boolean PREF_BROWSER_AUGMENTED_DEFAULT = true;
        static final int PREF_DL_THREADS_QUANTITY_DEFAULT = Constant.DOWNLOAD_THREAD_COUNT_AUTO;
        static final int PREF_FOLDER_TRUNCATION_DEFAULT = Constant.TRUNCATE_FOLDER_NONE;
        static final boolean PREF_VIEWER_RESUME_LAST_LEFT = true;
        static final boolean PREF_VIEWER_KEEP_SCREEN_ON = true;
        static final int PREF_VIEWER_IMAGE_DISPLAY = Constant.PREF_VIEWER_DISPLAY_FIT;
        static final int PREF_VIEWER_BROWSE_MODE = Constant.PREF_VIEWER_BROWSE_NONE;
        static final boolean PREF_VIEWER_DISPLAY_PAGENUM = false;
        static final boolean PREF_VIEWER_TAP_TRANSITIONS = true;
        static final boolean PREF_VIEWER_OPEN_GALLERY = false;
        static final boolean PREF_VIEWER_SWIPE_TO_FLING = false;
        static final boolean PREF_VIEWER_INVERT_VOLUME_ROCKER = false;
        static final int PREF_DARK_MODE = (Build.VERSION.SDK_INT > P) ? Constant.DARK_MODE_DEVICE : Constant.DARK_MODE_OFF;
        static final boolean PREF_DL_RETRIES_ACTIVE = false;
        static final int PREF_DL_RETRIES_NUMBER = 3;
        static final int PREF_DL_RETRIES_MEM_LIMIT = 100;
        static final boolean PREF_DL_HITOMI_WEBP = true;
        static final boolean PREF_CHECK_UPDATES_DEFAULT = true;
        // Default menu in v1.9.x
        static final Site[] DEFAULT_SITES = new Site[]{Site.NHENTAI, Site.HENTAICAFE, Site.HITOMI, Site.ASMHENTAI, Site.TSUMINO, Site.PURURIN, Site.EHENTAI, Site.FAKKU2, Site.NEXUS, Site.MUSES, Site.DOUJINS};
        static final String ACTIVE_SITES = TextUtils.join(",", Stream.of(DEFAULT_SITES).map(Site::getCode).toList());
    }

    // IMPORTANT : Any value change must be mirrored in res/values/array_preferences.xml
    public static final class Constant {

        private Constant() {
            throw new IllegalStateException("Utility class");
        }

        public static final int DOWNLOAD_THREAD_COUNT_AUTO = 0;
        public static final int ORDER_CONTENT_FAVOURITE = -2; // Artificial order created for clarity purposes
        public static final int ORDER_CONTENT_NONE = -1;
        public static final int ORDER_CONTENT_TITLE_ALPHA = 0;
        public static final int ORDER_CONTENT_LAST_DL_DATE_FIRST = 1;
        public static final int ORDER_CONTENT_TITLE_ALPHA_INVERTED = 2;
        public static final int ORDER_CONTENT_LAST_DL_DATE_LAST = 3;
        public static final int ORDER_CONTENT_RANDOM = 4;
        public static final int ORDER_CONTENT_LAST_UL_DATE_FIRST = 5;
        public static final int ORDER_CONTENT_LEAST_READ = 6;
        public static final int ORDER_CONTENT_MOST_READ = 7;
        public static final int ORDER_CONTENT_LAST_READ = 8;
        public static final int ORDER_CONTENT_PAGES_DESC = 9;
        public static final int ORDER_CONTENT_PAGES_ASC = 10;
        public static final int ORDER_ATTRIBUTES_ALPHABETIC = 0;
        static final int ORDER_ATTRIBUTES_COUNT = 1;
        static final int PREF_FOLDER_NAMING_CONTENT_ID = 0;
        static final int PREF_FOLDER_NAMING_CONTENT_TITLE_ID = 1;
        static final int PREF_FOLDER_NAMING_CONTENT_AUTH_TITLE_ID = 2;
        static final int TRUNCATE_FOLDER_NONE = 0;
        public static final int PREF_VIEWER_DISPLAY_FIT = 0;
        public static final int PREF_VIEWER_DISPLAY_FILL = 1;
        public static final int PREF_VIEWER_BROWSE_NONE = -1;
        public static final int PREF_VIEWER_BROWSE_LTR = 0;
        public static final int PREF_VIEWER_BROWSE_RTL = 1;
        public static final int PREF_VIEWER_BROWSE_TTB = 2;
        public static final int PREF_VIEWER_DIRECTION_LTR = 0;
        public static final int PREF_VIEWER_DIRECTION_RTL = 1;
        public static final int PREF_VIEWER_ORIENTATION_HORIZONTAL = 0;
        public static final int PREF_VIEWER_ORIENTATION_VERTICAL = 1;
        public static final int DARK_MODE_OFF = 0;
        public static final int DARK_MODE_ON = 1;
        public static final int DARK_MODE_BATTERY = 2;
        static final int DARK_MODE_DEVICE = 3;
    }
}
