package com.github.ntsee.sheetchef;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisSplitPane;

import java.util.List;
import java.util.Optional;

public class SheetChefView implements Disposable {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    public static final int PADDING = 10;

    private final Viewport viewport;
    private final ShapeRenderer shapes;
    private final Batch batch;
    private final Stage stage;
    private final SideBar sideBar;
    private final VisImage image;
    private final VisSplitPane pane;
    private final Container<VisSplitPane> root;
    private Texture texture;

    public SheetChefView(Listener listener) {
        VisUI.load(VisUI.SkinScale.X1);
        this.viewport = new ExtendViewport(WIDTH, HEIGHT);
        this.shapes = new ShapeRenderer();
        this.batch = new SpriteBatch();
        this.stage = new Stage(this.viewport, this.batch);
        this.stage.setActionsRequestRendering(true);
        this.sideBar = new SideBar(listener);
        this.image = new VisImage(null, Scaling.fit);
        this.pane = new VisSplitPane(this.sideBar, this.image, false);
        this.pane.setMaxSplitAmount(1 / 2f);
        this.pane.setMinSplitAmount(Value.prefWidth.get(this.sideBar) / WIDTH);
        this.root = new Container<>(this.pane).padLeft(PADDING).padRight(PADDING).fill();
        this.root.setFillParent(true);
        this.stage.addActor(this.root);
        Gdx.graphics.setContinuousRendering(false);
        Gdx.input.setInputProcessor(this.stage);
    }

    public void showInputImage(Texture texture) {
        if (this.texture != null) {
            this.texture.dispose();
        }

        this.texture = texture;
        this.image.setDrawable(new TextureRegionDrawable(new TextureRegion(this.texture)));
        Gdx.graphics.requestRendering();
    }

    public void setBackgroundColor(Color color) {
        this.sideBar.setBackgroundColor(color);
    }

    public void setExportFormat(ExportFormat format) {
        this.sideBar.setExportFormat(format);
    }

    public void setExportDirectory(FileHandle handle) {
        this.sideBar.setExportDirectory(handle);
    }

    public void showInvalidInputFile(Throwable err) {
        this.sideBar.setInputFile(null);
        Dialogs.showErrorDialog(this.stage, "Failed to open input image", err);
    }

    public void showInvalidExportDirectory(Throwable err) {
        this.sideBar.setExportDirectory(null);
        Dialogs.showErrorDialog(this.stage, "Failed to set export directory", err);
    }

    public void showExportFailure(Throwable err) {
        Dialogs.showErrorDialog(this.stage, "Failed to export files", err);
    }

    public void showExportSuccess() {
        Dialogs.showOKDialog(this.stage, "Export Successful",
                "Your sliced sheet has been successfully saved to the export directory.");
    }

    public void update() {
        this.stage.act();
    }

    public void render() {
        ScreenUtils.clear(Color.BLACK);
        this.stage.draw();
    }

    public void resize(int width, int height) {
        this.viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        VisUI.dispose(true);
        this.shapes.dispose();
        this.batch.dispose();
        this.stage.dispose();
        if (this.texture != null) {
            this.texture.dispose();
        }
    }

    public static Texture createImageOverlay(Pixmap src, List<Rectangle> boxes) {
        Pixmap pixmap = new Pixmap(src.getWidth(), src.getHeight(), src.getFormat());
        pixmap.drawPixmap(src, 0, 0);
        pixmap.setColor(Color.RED);
        for (Rectangle box : boxes) {
            pixmap.drawRectangle((int)box.x, (int)box.y, (int)box.width, (int)box.height);
        }

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    public interface Listener {

        void onInputFileChanged(FileHandle handle);
        void onBackgroundColorChanged(Color color);
        void onExportFormatChanged(ExportFormat format);
        void onExportDirectoryChanged(FileHandle handle);
        void onExportClicked();
    }
}
