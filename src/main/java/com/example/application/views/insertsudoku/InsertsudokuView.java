package com.example.application.views.insertsudoku;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.example.application.views.main.MainView;
import com.vaadin.flow.component.dependency.CssImport;

@Route(value = "insert", layout = MainView.class)
@PageTitle("Insert sudoku")
@CssImport("./views/insertsudoku/insertsudoku-view.css")
public class InsertsudokuView extends Div {

    public InsertsudokuView() {
        addClassName("insertsudoku-view");
        add(new Text("Content placeholder"));
    }

}
