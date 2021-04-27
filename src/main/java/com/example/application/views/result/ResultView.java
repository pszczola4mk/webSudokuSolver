package com.example.application.views.result;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.example.application.views.main.MainView;
import com.vaadin.flow.component.dependency.CssImport;

@Route(value = "result", layout = MainView.class)
@PageTitle("Result")
@CssImport("./views/result/result-view.css")
public class ResultView extends Div {

    public ResultView() {
        addClassName("result-view");
        add(new Text("Content placeholder"));
    }

}
