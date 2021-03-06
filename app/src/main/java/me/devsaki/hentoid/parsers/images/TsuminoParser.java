package me.devsaki.hentoid.parsers.images;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.devsaki.hentoid.database.domains.Content;

import static me.devsaki.hentoid.util.HttpHelper.getOnlineDocument;

/**
 * Created by Shiro on 1/22/2016.
 * Handles parsing of content from tsumino
 */
public class TsuminoParser extends BaseParser {

    @Override
    protected List<String> parseImages(Content content) throws Exception {
        Document doc = getOnlineDocument(content.getReaderUrl());
        if (null != doc) {
            Elements captcha = doc.select(".g-recaptcha");
            if (captcha != null && !captcha.isEmpty())
                throw new UnsupportedOperationException("Captcha found");

            Element contents = doc.select("#image-container").first();
            if (null != contents) {
                String imgTemplate = contents.attr("data-cdn");
                return buildImageUrls(imgTemplate, content);
            }
        }
        return Collections.emptyList();
    }

    private static List<String> buildImageUrls(String imgTemplate, Content content) {
        List<String> imgUrls = new ArrayList<>();

        for (int i = 0; i < content.getQtyPages(); i++)
            imgUrls.add(imgTemplate.replace("[PAGE]", i + 1 + ""));

        return imgUrls;
    }
}
