package pl.wojo.views.insertsudoku;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import pl.wojo.business.ImageClient;
import pl.wojo.views.main.MainView;
import com.vaadin.flow.component.dependency.CssImport;

@Route(value = "insert", layout = MainView.class)
@PageTitle("Insert sudoku")
@CssImport("./views/insertsudoku/insertsudoku-view.css")
@Slf4j
public class InsertsudokuView extends Div {

    @Autowired
    private ImageClient imageClient;
    private List<TextField> sudokuFields = new ArrayList<>();
    private final Element resolvePlaceholder;

    public InsertsudokuView() {
        addClassName("insertsudoku-view");
        //add(new Text("Content placeholder"));
        for(int i=0;i<9;i++) {
            HorizontalLayout vl = new HorizontalLayout();
            for(int j=0;j<9;j++) {
                TextField tf = new TextField();
                tf.setWidth("50px");
                vl.add(tf);
                this.sudokuFields.add(tf);
            }
            add(vl);
        }
        Button button = new Button("Resolve");
        button.addClickListener(this::resolveSudoku);
        add(button);
        this.resolvePlaceholder = new Element("table");
        getElement().appendChild(this.resolvePlaceholder);
    }

    private void resolveSudoku(ClickEvent<Button> buttonClickEvent) {
        log.info("resolveSudoku");
        String line = "";
       String sudoku = "";
        for(int i=0;i<81;i++) {
            line=line+this.sudokuFields.get(i).getValue()+"|";
            if(i%9==8){
                sudoku=sudoku+line+";";
                line="";
            }
        }
        log.info(sudoku);
        String resolvedSudoku = this.imageClient.resolveText(sudoku);
        insertToTable(resolvedSudoku);
    }

    private void insertToTable(String resolvedSudoku) {
        this.resolvePlaceholder.removeAllChildren();
        String[] rows = resolvedSudoku.split(";");
        for (String row : rows) {
            Element tableRow = new Element("tr");
            String[] cols = row.split("\\|");
            for (String col : cols) {
                if (StringUtils.isNotEmpty(col)) {
                    Element tableCell = new Element("td");
                    tableCell.setText(col);
                    tableRow.appendChild(tableCell);
                }
            }
            this.resolvePlaceholder.appendChild(tableRow);
        }
    }

}
