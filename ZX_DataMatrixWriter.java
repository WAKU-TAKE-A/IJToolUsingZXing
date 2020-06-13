import ij.*;
import ij.IJ;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilterRunner;
import ij.process.ImageProcessor;

import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.DataMatrixWriter;
import com.google.zxing.datamatrix.encoder.SymbolShapeHint;

/*
 * The MIT License
 *
 * Copyright 2020 Takehito Nishida.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/**
 * com.google.zxing.datamatrix.DataMatrixWriter.
 */
public class ZX_DataMatrixWriter implements ij.plugin.filter.ExtendedPlugInFilter {
    // constant var.
    private static final int FLAGS = NO_IMAGE_REQUIRED;    
    private static final SymbolShapeHint[] HINTS_SHAPE = { SymbolShapeHint.FORCE_NONE, SymbolShapeHint.FORCE_SQUARE, SymbolShapeHint.FORCE_RECTANGLE };
    private static final String[] STR_HINTS_SHAPE = { "FORCE_NONE", "FORCE_SQUARE", "FORCE_RECTANGLE" };
     
    // staic var.
    private static String content = "abc123";
    private static int sizeW = 128;
    private static int sizeH = 128;
    private static int indHintsShape = 1;
    
    // var.
    String cmd = "";

    @Override
    public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr) {
        cmd = command.trim();
        
        GenericDialog gd = new GenericDialog(cmd + " ...");        
        gd.addStringField("content", content);
        gd.addNumericField("width", sizeW, 0);
        gd.addNumericField("height", sizeH, 0);
        gd.addChoice("shape", STR_HINTS_SHAPE, STR_HINTS_SHAPE[indHintsShape]);
        gd.showDialog();

        if (gd.wasCanceled())
        {
            return DONE;
        }
        else
        {
            content = gd.getNextString();
            sizeW = (int)gd.getNextNumber();
            sizeH = (int)gd.getNextNumber();
            indHintsShape = (int)gd.getNextChoiceIndex();
            return IJ.setupDialog(imp, FLAGS);
        }
    }
    
    @Override
    public void setNPasses(int nPasses) {
        // do nothing
    }

    @Override
    public int setup(String arg, ImagePlus imp) {
        return FLAGS;
    }

    @Override
    public void run(ImageProcessor ip) {        
        try {
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.DATA_MATRIX_SHAPE, HINTS_SHAPE[indHintsShape]);
            
            DataMatrixWriter writer = new DataMatrixWriter();
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.DATA_MATRIX, sizeW, sizeH, hints);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
          
            String titleRet = WindowManager.getUniqueName("DATA_MATRIX");
            ImagePlus ret = new ImagePlus(titleRet, image);
            ret.show();
        }
        catch(Exception ex) {
            IJ.error(ex.toString());
        }
    }
}
