package com.softannate.apppuentedecomunicacion.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.File;

public class PdfUtils {

    public static Bitmap renderPaginaPDF(Context context, String rutaPdf) {
        try {
            File file = new File(rutaPdf);
            if (!file.exists()) return null;

            ParcelFileDescriptor fd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer renderer = new PdfRenderer(fd);
            PdfRenderer.Page page = renderer.openPage(0);

            Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            page.close();
            renderer.close();
            fd.close();

            return bitmap;
        } catch (Exception e) {
            Log.e("PDFPreview", "Error al renderizar PDF: " + e.getMessage());
            return null;
        }
    }
}
