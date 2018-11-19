package models;

/**
 * Created by mike on 27 Feb 2018.
 */

public class Category {
String categoryName;
    int color;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Category()
    {

   }
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }


    public boolean equals(Object obj){
        if (obj instanceof Category) {
            Category pp = (Category) obj;
            return (pp.categoryName.equals(this.categoryName) );
        } else {
            return false;
        }
    }
}
