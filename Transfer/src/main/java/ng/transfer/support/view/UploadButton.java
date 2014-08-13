package ng.transfer.support.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;

import ng.transfer.R;
import ng.transfer.support.info.Defines;
import ng.transfer.support.info.Transfer;
import ng.transfer.support.util.TransferUtils;

/**
 * Created by Joe on 2014/6/10.
 */
public class UploadButton extends Button {

    private static final float ARC_WIDTH = 6;
    private float progress = Defines.STATUS_RESET;
    private String progressStr = "0/0";
    private Paint paint;
    private TextPaint textPaint;
    private RectF rectF;
    private boolean init = false;
    private int canvasHeight;
    private int canvasWidth;
    private int colorHoloGreen;
    private int colorHoloRed;
    private float uploadType;

    public UploadButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        colorHoloGreen = Transfer.getResColor(R.color.holo_green);
        colorHoloRed = Transfer.getResColor(R.color.holo_red);

        paint = new Paint();
        paint.setAntiAlias(true);
//        paint.setColor(Transfer.getResColor(R.color.holo_green));
//        paint.setStrokeWidth(ARC_WIDTH);
//        paint.setStyle(Paint.Style.STROKE);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(colorHoloGreen);

        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(40);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);

        rectF = new RectF();


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!init) {
            canvasHeight = (int)Transfer.getGlobalContext().getResources().getDimension(R.dimen.btn_upload_height);
            canvasWidth = (int)Transfer.getGlobalContext().getResources().getDimension(R.dimen.btn_upload_width);
            rectF.left = 0;
            rectF.top = 0;
            rectF.right = (int)Transfer.getGlobalContext().getResources().getDimension(R.dimen.btn_upload_width);
            rectF.bottom = (int)Transfer.getGlobalContext().getResources().getDimension(R.dimen.btn_upload_height);
            Log.e("RECT_F", String.valueOf(rectF.left));
            Log.e("RECT_F", String.valueOf(rectF.top));
            Log.e("RECT_F", String.valueOf(rectF.right));
            Log.e("RECT_F", String.valueOf(rectF.bottom));
            init = true;
        }

        //画扇形
        paint.setColor(colorHoloGreen);

        if (progress == Defines.STATUS_FAILED) {
            paint.setColor(colorHoloRed);
            canvas.drawArc(rectF, 270, 360, true, paint);
        } else if (progress == Defines.STATUS_RESET) {
            canvas.drawArc(rectF, 270, 0, false, paint);
        } else if (uploadType == Defines.STATUS_UPLOADING_VIDEOS) {
            canvas.drawArc(rectF, 270, progress, true, paint);
        } else if (uploadType == Defines.STATUS_UPLOADING_IMAGES) {
            int currentIndex = Integer.valueOf(progressStr.substring(0, progressStr.indexOf('/')));
            int allFilesCount = Integer.valueOf(progressStr.substring(progressStr.indexOf('/') + 1, progressStr.length()));
            canvas.drawArc(rectF, 270, ((float) currentIndex / (float) allFilesCount) * 360, true, paint);
        } else if(progress == Defines.STATUS_FINISHED){
            paint.setColor(colorHoloGreen);
            canvas.drawArc(rectF, 270, 360, true, paint);
        } else
            canvas.drawArc(rectF, 270, (float) (progress * 3.6), true, paint);

        //画字
        if (uploadType == Defines.STATUS_UPLOADING_IMAGES) {
            drawButtonText(Transfer.getResString(R.string.btn_upload_upload_images) + ' ' + progressStr, canvas);
        } else if (uploadType == Defines.STATUS_UPLOADING_VIDEOS) {
            drawButtonText(Transfer.getResString(R.string.btn_upload_upload_videos) + ' ' + progressStr, canvas);
        } else {
            if (progress == Defines.STATUS_RESET) {
                drawButtonText(Transfer.getResString(R.string.btn_upload_start), canvas);
                setEnabled(true);
            } else if (progress == Defines.STATUS_READY) {
//            setEnabled(false);
                drawButtonText(Transfer.getResString(R.string.btn_upload_ready), canvas);
            } else if (progress > 0 && progress <= 100) {
                drawButtonText(Transfer.getResString(R.string.btn_upload_uploading) + '\n' + progress + '%', canvas);
            } else if (progress == Defines.STATUS_FINISHED) {
                drawButtonText(Transfer.getResString(R.string.btn_upload_finished), canvas);
            } else if (progress == Defines.STATUS_FAILED) {
                drawButtonText(Transfer.getResString(R.string.btn_upload_failed), canvas);
            } else if (progress == Defines.STATUS_SCAN_IMAGES) {
                drawButtonText(Transfer.getResString(R.string.btn_upload_scan_images), canvas);
            } else if (progress == Defines.STATUS_SCAN_VIDEOS) {
                drawButtonText(Transfer.getResString(R.string.btn_upload_scan_videos), canvas);
            }
        }
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    public void setProgressString(String progressStr) {
        this.progressStr = progressStr;
        invalidate();
    }

    public void setUploadType(float type) {
        uploadType = type;
        invalidate();
    }

    private void drawButtonText(String text, Canvas canvas) {
        canvas.drawText(text, canvasWidth / 2, (int) ((canvasHeight / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)), textPaint);
    }

}
