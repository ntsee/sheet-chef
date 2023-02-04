package com.github.ntsee.sheetchef;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;
import com.kotcrab.vis.ui.widget.color.ColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;

public class SideBar extends VisTable {

    private final SheetChefView.Listener listener;
    private final VisLabel lblInputFile;
    private final VisTextField tfInputFile;
    private final VisTextButton btnInputFile;
    private final FileChooser fcInputFile;
    private final VisLabel lblBackground;
    private final Button btnBackground;
    private final ColorPicker colorPicker;
    private final VisLabel lblExportFormat;
    private final VisSelectBox<ExportFormat> sbExportFormat;
    private final VisLabel lblExportDirectory;
    private final VisTextField tfExportDirectory;
    private final VisTextButton btnExportDirectory;
    private final FileChooser fcExportDirectory;
    private final VisTextButton btnExport;

    public SideBar(SheetChefView.Listener listener) {
        super(true);
        FileChooser.setDefaultPrefsName("SheetChef.xml");
        this.listener = listener;
        this.lblInputFile = new VisLabel("Input Image Location");
        this.lblInputFile.setAlignment(Align.center);
        this.tfInputFile = new VisTextField();
        this.tfInputFile.setReadOnly(true);
        this.tfInputFile.setAlignment(Align.center);
        this.btnInputFile = new VisTextButton("Browse");
        this.fcInputFile = new FileChooser(FileChooser.Mode.OPEN);
        this.fcInputFile.setDirectory(Gdx.files.getLocalStoragePath());
        this.fcInputFile.setMultiSelectionEnabled(false);
        this.lblBackground = new VisLabel("Background Color");
        this.lblBackground.setAlignment(Align.center);
        this.btnBackground = new Button(VisUI.getSkin().getDrawable("white"));
        this.colorPicker = new ColorPicker();
        this.lblExportFormat = new VisLabel("Export Format");
        this.lblExportFormat.setAlignment(Align.center);
        this.sbExportFormat = new VisSelectBox<>();
        this.sbExportFormat.setAlignment(Align.center);
        this.sbExportFormat.setItems(ExportFormat.values());
        this.lblExportDirectory = new VisLabel("Export Directory Location");
        this.lblExportDirectory.setAlignment(Align.center);
        this.tfExportDirectory = new VisTextField();
        this.tfExportDirectory.setReadOnly(true);
        this.btnExportDirectory = new VisTextButton("Browse");
        this.fcExportDirectory = new FileChooser(FileChooser.Mode.OPEN);
        this.fcExportDirectory.setMultiSelectionEnabled(false);
        this.fcExportDirectory.setSelectionMode(FileChooser.SelectionMode.DIRECTORIES);
        this.btnExport = new VisTextButton("Export");
        this.initialize();
    }

    public void setInputFile(FileHandle handle) {
        if (handle == null) {
            this.tfInputFile.setText(null);
        } else {
            this.tfInputFile.setText(handle.path());
        }
    }

    public void setBackgroundColor(Color color) {
        this.btnBackground.setColor(color);
    }

    public void setExportFormat(ExportFormat format) {
        this.sbExportFormat.setSelected(format);
    }

    public void setExportDirectory(FileHandle handle) {
        if (handle == null) {
            this.tfExportDirectory.setText(null);
        } else {
            this.fcExportDirectory.setDirectory(handle);
            this.tfExportDirectory.setText(handle.path());
        }
    }

    private void initialize() {
        this.addChildrenToParent();
        this.addInputFileListener();
        this.addBackgroundColorListener();
        this.addExportFormatListener();
        this.addExportDirectoryListener();
        this.addExportButtonListener();
    }

    private void addChildrenToParent() {
        this.top().padLeft(SheetChefView.PADDING / 2f).padRight(SheetChefView.PADDING / 2f);
        this.defaults().top().center().growX();
        this.add(this.lblInputFile).row();
        this.add(this.tfInputFile).row();
        this.add(this.btnInputFile).row();
        this.add(this.lblBackground).row();
        this.add(this.btnBackground).height(this.tfInputFile.getHeight()).row();
        this.add(this.lblExportFormat).row();
        this.add(this.sbExportFormat).row();
        this.add(this.lblExportDirectory).row();
        this.add(this.tfExportDirectory).row();
        this.add(this.btnExportDirectory).row();
        this.add(this.btnExport).row();
    }

    private void addInputFileListener() {
        this.fcInputFile.setListener(new FileChooserAdapter() {
            @Override
            public void selected(Array<FileHandle> files) {
                FileHandle handle = files.first();
                tfInputFile.setText(handle.path());
                listener.onInputFileChanged(handle);
            }
        });

        this.btnInputFile.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getStage().addActor(fcInputFile.fadeIn());
            }
        });
    }

    private void addBackgroundColorListener() {
        this.colorPicker.setListener(new ColorPickerAdapter() {
            @Override
            public void finished(Color newColor) {
                btnBackground.setColor(newColor);
                listener.onBackgroundColorChanged(newColor);
            }
        });

        this.btnBackground.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getStage().addActor(colorPicker.fadeIn());
            }
        });
    }

    private void addExportFormatListener() {
        this.sbExportFormat.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ExportFormat exportFormat = sbExportFormat.getSelected();
                listener.onExportFormatChanged(exportFormat);
            }
        });
    }

    private void addExportDirectoryListener() {
        this.fcExportDirectory.setListener(new FileChooserAdapter() {
            @Override
            public void selected(Array<FileHandle> files) {
                FileHandle handle = files.first();
                tfExportDirectory.setText(handle.path());
                listener.onExportDirectoryChanged(handle);
            }
        });

        this.btnExportDirectory.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                getStage().addActor(fcExportDirectory.fadeIn());
            }
        });
    }

    private void addExportButtonListener() {
        this.btnExport.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                listener.onExportClicked();
            }
        });
    }
}
