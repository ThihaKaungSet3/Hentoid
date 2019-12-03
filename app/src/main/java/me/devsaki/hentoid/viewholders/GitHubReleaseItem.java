package me.devsaki.hentoid.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.squareup.moshi.Json;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;

import me.devsaki.hentoid.R;

import static androidx.core.view.ViewCompat.requireViewById;

public class GitHubReleaseItem extends AbstractItem<GitHubReleaseItem.ReleaseViewHolder> {

    private final String tagName;
    private final String name;
    private final String description;
    private final Date creationDate;

    public GitHubReleaseItem(GitHubRelease.Struct releaseStruct) {
        tagName = releaseStruct.tagName.replace("v", "");
        name = releaseStruct.name;
        description = releaseStruct.body;
        creationDate = releaseStruct.creationDate;
    }

    public String getTagName() {
        return tagName;
    }

    public boolean isTagPrior(@Nonnull String tagName) {
        return getIntFromTagName(this.tagName) <= getIntFromTagName(tagName);
    }

    private static int getIntFromTagName(@Nonnull String tagName) {
        int result = 0;
        String[] parts = tagName.split("\\.");
        if (parts.length > 0) result = 10000 * Integer.parseInt(parts[0].replaceAll("[^\\d]", ""));
        if (parts.length > 1) result += 100 * Integer.parseInt(parts[1].replaceAll("[^\\d]", ""));
        if (parts.length > 2) result += Integer.parseInt(parts[2].replaceAll("[^\\d]", ""));

        return result;
    }

    @NotNull
    @Override
    public ReleaseViewHolder getViewHolder(@NotNull View view) {
        return new ReleaseViewHolder(view);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_changelog;
    }

    @Override
    public int getType() {
        return R.id.github_release;
    }


    static class ReleaseViewHolder extends FastAdapter.ViewHolder<GitHubReleaseItem> {

        private final TextView title;
        private final ItemAdapter<GitHubReleaseDescItem> itemAdapter = new ItemAdapter<>();

        ReleaseViewHolder(View view) {
            super(view);
            title = requireViewById(view, R.id.changelogReleaseTitle);

            FastAdapter<GitHubReleaseDescItem> releaseDescriptionAdapter = FastAdapter.with(itemAdapter);
            RecyclerView releasedDescription = requireViewById(view, R.id.changelogReleaseDescription);
            releasedDescription.setAdapter(releaseDescriptionAdapter);
        }


        @Override
        public void bindView(@NotNull GitHubReleaseItem item, @NotNull List<Object> list) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

            setTitle(item.name + " (" + dateFormat.format(item.creationDate) + ")");

            clearContent();
            // Parse content and add lines to the description
            for (String s : item.description.split("\\r\\n")) { // TODO - refactor this code with its copy in UpdateSuccessDialogFragment
                s = s.trim();
                if (s.startsWith("-")) addListContent(s);
                else addDescContent(s);
            }
        }

        public void setTitle(String title) {
            this.title.setText(title);
        }

        void clearContent() {
            itemAdapter.clear();
        }

        void addDescContent(String text) {
            itemAdapter.add(new GitHubReleaseDescItem(text, GitHubReleaseDescItem.Type.DESCRIPTION));
        }

        void addListContent(String text) {
            itemAdapter.add(new GitHubReleaseDescItem(text, GitHubReleaseDescItem.Type.LIST_ITEM));
        }

        @Override
        public void unbindView(@NotNull GitHubReleaseItem item) {

        }
    }

    public static class Struct {
        @Json(name = "tag_name")
        String tagName;
        String name;
        String body;
        @Json(name = "created_at")
        Date creationDate;

        public String getName() {
            return name;
        }

        public String getBody() {
            return body;
        }
    }
}
