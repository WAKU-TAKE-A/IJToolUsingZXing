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
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

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
 * com.google.zxing.qrcode.QRCodeWriter.
 */
public class ZX_QRCodeWriter implements ij.plugin.filter.ExtendedPlugInFilter {
    // constant var.
    private static final int FLAGS = NO_IMAGE_REQUIRED;
    private static final ErrorCorrectionLevel[] HINTS_ERRCOR = { ErrorCorrectionLevel.L, ErrorCorrectionLevel.M, ErrorCorrectionLevel.Q, ErrorCorrectionLevel.H };
    private static final String[] STR_HINTS_ERRCOR = { "L", "M", "Q", "H" };
     
    // staic var.
    private static String content = "abc123";
    private static int sizeW = 128;
    private static int sizeH = 128;
    private static int margin = 3;
    private static int indHintsErr = 3;
    private static Boolean isGS1 = false;
    
    // var.
    String cmd = "";

    @Override
    public int showDialog(ImagePlus imp, String command, PlugInFilterRunner pfr) {
        cmd = command.trim();
        
        GenericDialog gd = new GenericDialog(cmd + " ...");        
        gd.addStringField("content", content);
        gd.addNumericField("width", sizeW, 0);
        gd.addNumericField("height", sizeH, 0);
        gd.addNumericField("margin", margin, 0);
        gd.addChoice("level", STR_HINTS_ERRCOR, STR_HINTS_ERRCOR[indHintsErr]);
        gd.addCheckbox("gs1", isGS1);
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
            margin = (int)gd.getNextNumber();
            indHintsErr = (int)gd.getNextChoiceIndex();  
            isGS1 = gd.getNextBoolean();
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
            hints.put(EncodeHintType.ERROR_CORRECTION, HINTS_ERRCOR[indHintsErr]);
            hints.put(EncodeHintType.MARGIN, margin);
            hints.put(EncodeHintType.GS1_FORMAT, isGS1);

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, sizeW, sizeH, hints);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
          
            String titleRet = WindowManager.getUniqueName("QR_CODE");
            ImagePlus ret = new ImagePlus(titleRet, image);
            ret.show();
        }
        catch(WriterException ex) {
            IJ.error(ex.toString());
        }
    }
}
