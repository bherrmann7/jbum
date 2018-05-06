package jbum.layouts

import jbum.core.ImageInfo2

/** written for modeling and writing out a JSON structure */

 class Page {
    String title;

    String intro;

    int picsPerRow;

    String bgColor;

    String textColor;

    String panelColor;

    String prolog;

    String where;

    ArrayList<ImageInfo2> photos;


     Page(String title, String intro, ArrayList<ImageInfo2> photos, int picsPerRow, String bgColor, String textColor, String panelColor, String prolog, String where) {
        super();
        this.title = title;
        this.intro = intro.replace('\n', ' ');
        this.photos = photos;
        this.picsPerRow = picsPerRow;
        this.bgColor = bgColor;
        this.textColor = textColor;
        this.panelColor = panelColor;
        this.prolog = prolog.replace('\n', ' ');
        this.where = where;
    }

}
