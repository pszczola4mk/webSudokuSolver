package pl.wojo.views.uploadsudoku;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.internal.MessageDigestUtil;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamResource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import pl.wojo.business.ImageClient;
import pl.wojo.views.main.MainView;

@Route(value = "upload", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Upload sudoku")
@CssImport("./views/uploadsudoku/uploadsudoku-view.css")
@Slf4j
public class UploadsudokuView extends Div {

    private final Element resolvePlaceholder;
    private String base64Image;
    @Autowired
    private ImageClient imageClient;

    public UploadsudokuView() {
        addClassName("uploadsudoku-view");
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setMaxFiles(1);
        upload.setDropLabel(new Label("Upload a 500 kb file in .png format"));
        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        upload.setMaxFileSize(500000);
        Div output = new Div();

        upload.addSucceededListener(event -> {
            Component component = createComponent(event.getMIMEType(),
                    event.getFileName(), buffer.getInputStream());
            output.removeAll();
            showOutput(event.getFileName(), component, output);
        });

        upload.addFileRejectedListener(event -> {
            Paragraph component = new Paragraph();
            showOutput(event.getErrorMessage(), component, output);
        });
        upload.getElement().addEventListener("file-remove", event -> {
            output.removeAll();
        });
        add(upload, output);
        Button button = new Button("Resolve");
        button.addClickListener(this::resolveSudoku);
        add(button);
        this.resolvePlaceholder = new Element("table");
        getElement().appendChild(this.resolvePlaceholder);
    }

    private void resolveSudoku(ClickEvent<Button> buttonClickEvent) {
        log.info("resolveSudoku");
        if (this.base64Image != null) {
            String resolvedSudoku = this.imageClient.resolveSudoku(new pl.wojo.business.model.Image("sudoku", base64Image));
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

    private Component createComponent(String mimeType, String fileName, InputStream stream) {
        if (mimeType.startsWith("image")) {
            Image image = new Image();
            try {
                byte[] bytes = IOUtils.toByteArray(stream);
                convertToBase64(bytes);
                image.getElement().setAttribute("src", new StreamResource(
                        fileName, () -> new ByteArrayInputStream(bytes)));
                try (ImageInputStream in = ImageIO.createImageInputStream(
                        new ByteArrayInputStream(bytes))) {
                    final Iterator<ImageReader> readers = ImageIO
                            .getImageReaders(in);
                    if (readers.hasNext()) {
                        ImageReader reader = readers.next();
                        try {
                            reader.setInput(in);
                            image.setWidth(reader.getWidth(0) + "px");
                            image.setHeight(reader.getHeight(0) + "px");
                        } finally {
                            reader.dispose();
                        }
                    }
                }
            } catch (IOException e) {
               log.error("Error during img upload: "+e.getMessage(),e);
            }
            image.setSizeFull();
            return image;
        }
        Div content = new Div();
        String text = String.format("Mime type: '%s'\nSHA-256 hash: '%s'",
                mimeType, MessageDigestUtil.sha256(stream.toString()));
        content.setText(text);
        return content;

    }

    private void convertToBase64(byte[] imageData) {
        this.base64Image = Base64.getEncoder().encodeToString(imageData);
    }


    private void showOutput(String text, Component content, HasComponents outputContainer) {
        HtmlComponent p = new HtmlComponent(Tag.P);
        p.getElement().setText(text);
        outputContainer.add(p);
        outputContainer.add(content);
    }

}
