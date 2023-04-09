package com.github.ntsee.sheetchef;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.github.ntsee.sheetchef.exceptions.FileFormatException;

import java.io.IOException;

public class SheetChefApp extends ApplicationAdapter implements SheetChefView.Listener {

	private ImageSlicer slicer;
	private SheetChefView view;

	@Override
	public void create () {
		this.slicer = new ImageSlicer();
		this.view = new SheetChefView(this);
		this.view.setBackgroundColor(this.slicer.getBackgroundColor());
		this.view.setExportFormat(this.slicer.getExportFormat());
		this.view.setExportDirectory(this.slicer.getExportDirectory());
	}

	@Override
	public void onInputFileChanged(FileHandle handle) {
		try {
			this.slicer.setInputImage(handle);
			Texture texture = SheetChefView.createImageOverlay(this.slicer.getInputImage(),
					this.slicer.getBoxes());
			this.view.showInputImage(texture);
		} catch (FileFormatException e) {
			this.view.showInvalidInputFile(e);
		}
	}

	@Override
	public void onBackgroundColorChanged(Color color) {
		this.slicer.setBackgroundColor(color);
		if (this.slicer.getInputImage() != null) {
			Texture texture = SheetChefView.createImageOverlay(this.slicer.getInputImage(),
					this.slicer.getBoxes());
			this.view.showInputImage(texture);
		}
	}

	@Override
	public void onExportFormatChanged(ExportFormat format) {
		this.slicer.setExportFormat(format);
	}

	@Override
	public void onExportDirectoryChanged(FileHandle handle) {
		try {
			this.slicer.setExportDirectory(handle);
		} catch (FileFormatException e) {
			this.view.showInvalidExportDirectory(e);
		}
	}

	@Override
	public void onExportClicked() {
		try {
			this.slicer.export();
			this.view.showExportSuccess();
		} catch (IOException e) {
			this.view.showExportFailure(e);
		}
	}

	@Override
	public void render () {
		this.view.update();
		this.view.render();
	}

	@Override
	public void resize(int width, int height) {
		this.view.resize(width, height);
	}

	@Override
	public void dispose () {
		this.slicer.dispose();
		this.view.dispose();
	}
}
