package me.devsaki.hentoid.fragments.library;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.SelectableAdapter;
import me.devsaki.hentoid.R;
import me.devsaki.hentoid.activities.AboutActivity;
import me.devsaki.hentoid.activities.DrawerEditActivity;
import me.devsaki.hentoid.activities.LibraryActivity;
import me.devsaki.hentoid.activities.PrefsActivity;
import me.devsaki.hentoid.activities.QueueActivity;
import me.devsaki.hentoid.enums.Site;
import me.devsaki.hentoid.events.UpdateEvent;
import me.devsaki.hentoid.util.Preferences;
import me.devsaki.hentoid.viewholders.DrawerItemFlex;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

public final class NavigationDrawerFragment extends Fragment {

    private LibraryActivity parentActivity;

    private FlexibleAdapter<DrawerItemFlex> drawerAdapter;

    // Settings listener
    private final SharedPreferences.OnSharedPreferenceChangeListener prefsListener = this::onSharedPreferenceChanged;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        parentActivity = (LibraryActivity) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        drawerAdapter = new FlexibleAdapter<>(null);
        drawerAdapter.setMode(SelectableAdapter.Mode.SINGLE);
        drawerAdapter.addListener((FlexibleAdapter.OnItemClickListener) this::onItemClick);

        DividerItemDecoration divider = new DividerItemDecoration(parentActivity, VERTICAL);

        Drawable d = ContextCompat.getDrawable(parentActivity, R.drawable.line_divider);
        if (d != null) divider.setDrawable(d);

        View rootView = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        View btn = rootView.findViewById(R.id.drawer_prefs_btn);
        btn.setOnClickListener(this::onPrefsClick);

        btn = rootView.findViewById(R.id.drawer_edit_btn);
        btn.setOnClickListener(this::onEditClick);

        RecyclerView recyclerView = rootView.findViewById(R.id.drawer_list);
        recyclerView.setAdapter(drawerAdapter);
        recyclerView.addItemDecoration(divider);

        updateItems();

        Preferences.registerPrefsChangedListener(prefsListener);

        return rootView;
    }

    private void updateItems() {
        List<DrawerItemFlex> drawerItems = new ArrayList<>();

        List<Site> sites = Preferences.getActiveSites();
        for (Site s : sites) drawerItems.add(new DrawerItemFlex(s));

        drawerItems.add(new DrawerItemFlex("QUEUE", R.drawable.ic_menu_queue, QueueActivity.class));
        drawerItems.add(new DrawerItemFlex("ABOUT", R.drawable.ic_menu_about, AboutActivity.class));

        drawerAdapter.clear();
        drawerAdapter.addItems(0, drawerItems);
    }

    private boolean onItemClick(View view, int position) {
        launchActivity(drawerAdapter.getItem(position).getActivityClass());
        return true;
    }

    private void launchActivity(@NonNull Class activityClass) {
        Intent intent = new Intent(parentActivity, activityClass);
        Bundle bundle = ActivityOptionsCompat
                .makeCustomAnimation(parentActivity, R.anim.fade_in, R.anim.fade_out)
                .toBundle();
        ContextCompat.startActivity(parentActivity, intent, bundle);

        parentActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        parentActivity.onNavigationDrawerItemClicked();
    }

    private void showFlagAboutItem() {
        if (drawerAdapter != null) {
            // About is always last
            int aboutItemPos = drawerAdapter.getItemCount() - 1;
            DrawerItemFlex item = drawerAdapter.getItem(aboutItemPos);
            if (item != null) {
                item.setFlag(true);
                drawerAdapter.notifyItemChanged(aboutItemPos);
            }
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onUpdateEvent(UpdateEvent event) {
        if (event.hasNewVersion) showFlagAboutItem();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);
        Preferences.unregisterPrefsChangedListener(prefsListener);
    }

    private void onPrefsClick(View view) {
        launchActivity(PrefsActivity.class);
    }

    private void onEditClick(View view) {
        launchActivity(DrawerEditActivity.class);
    }

    /**
     * Callback for any change in Preferences
     */
    private void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (Preferences.Key.ACTIVE_SITES.equals(key)) updateItems();
    }
}
