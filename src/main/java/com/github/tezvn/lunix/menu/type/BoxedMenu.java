package com.github.tezvn.lunix.menu.type;

public abstract class BoxedMenu<T> extends PaginationMenu<T> {

    public BoxedMenu(int page, int row, String title) {
        super(page, row, title);
    }


}