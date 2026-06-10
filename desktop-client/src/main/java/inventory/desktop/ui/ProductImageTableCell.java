package inventory.desktop.ui;

import inventory.desktop.context.AppContext;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import org.kordamp.ikonli.javafx.FontIcon;

public class ProductImageTableCell extends TableCell<ObservableList<String>, String> {
    private static final double IMAGE_SIZE = 50;

    @Override
    protected void updateItem(String path, boolean empty) {
        super.updateItem(path, empty);
        setText(null);

        if (empty) {
            setGraphic(null);
            return;
        }

        StackPane container = createContainer();
        setGraphic(container);

        if (path == null || path.trim().isEmpty()) {
            container.getChildren().setAll(createPlaceholder());
            return;
        }

        String expectedPath = path;
        Image image = new Image(AppContext.apiClient().resolvePublicUrl(path), IMAGE_SIZE, IMAGE_SIZE, true, true, true);
        ImageView imageView = createImageView(image);
        container.getChildren().setAll(createPlaceholder());

        image.progressProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() >= 1 && !image.isError() && expectedPath.equals(getItem())) {
                Platform.runLater(() -> container.getChildren().setAll(imageView));
            }
        });
        image.errorProperty().addListener((observable, oldValue, hasError) -> {
            if (Boolean.TRUE.equals(hasError) && expectedPath.equals(getItem())) {
                Platform.runLater(() -> container.getChildren().setAll(createPlaceholder()));
            }
        });

        if (image.getProgress() >= 1 && !image.isError() && expectedPath.equals(getItem())) {
            container.getChildren().setAll(imageView);
        }
    }

    private StackPane createContainer() {
        StackPane container = new StackPane();
        container.setAlignment(Pos.CENTER);
        container.setPrefSize(IMAGE_SIZE, IMAGE_SIZE);
        container.setMaxSize(IMAGE_SIZE, IMAGE_SIZE);
        container.getStyleClass().add("product-image-cell");
        return container;
    }

    private ImageView createImageView(Image image) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(IMAGE_SIZE);
        imageView.setFitHeight(IMAGE_SIZE);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        Rectangle clip = new Rectangle(IMAGE_SIZE, IMAGE_SIZE);
        clip.setArcWidth(14);
        clip.setArcHeight(14);
        imageView.setClip(clip);
        return imageView;
    }

    private Label createPlaceholder() {
        FontIcon icon = new FontIcon("fas-image");
        icon.setIconSize(18);
        icon.getStyleClass().add("product-image-placeholder-icon");
        Label label = new Label();
        label.setGraphic(icon);
        label.setAlignment(Pos.CENTER);
        label.getStyleClass().add("product-image-placeholder");
        label.setPrefSize(IMAGE_SIZE, IMAGE_SIZE);
        return label;
    }
}
