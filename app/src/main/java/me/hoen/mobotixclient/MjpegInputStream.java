package me.hoen.mobotixclient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class MjpegInputStream extends DataInputStream {
    private static final String TAG = "MjpegInputStream";

    private final byte[] SOI_MARKER = {(byte) 0xFF, (byte) 0xD8};
    private final byte[] EOF_MARKER = {(byte) 0xFF, (byte) 0xD9};
    private int mContentLength = -1;

    byte[] CONTENT_LENGTH_BYTES;
    byte[] CONTENT_END_BYTES;


    private final static int HEADER_MAX_LENGTH = 100;
    private final static int FRAME_MAX_LENGTH = 200000 + HEADER_MAX_LENGTH;
    private final String CONTENT_LENGTH = "Content-length:";
    private final String CONTENT_END = "\r\n";
    private final static byte[] gFrameData = new byte[FRAME_MAX_LENGTH];
    private final static byte[] gHeader = new byte[HEADER_MAX_LENGTH];
    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

    public MjpegInputStream(InputStream in) {
        super(new BufferedInputStream(in, FRAME_MAX_LENGTH));

        bitmapOptions.inSampleSize = 1;
        bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        bitmapOptions.inPreferQualityOverSpeed = false;
        bitmapOptions.inPurgeable = true;
        try {
            CONTENT_LENGTH_BYTES = CONTENT_LENGTH.getBytes("UTF-8");
            CONTENT_END_BYTES = CONTENT_END.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private int getEndOfSeqeunce(DataInputStream in, byte[] sequence) throws IOException {
        int seqIndex = 0;
        byte c;
        for (int i = 0; i < FRAME_MAX_LENGTH; i++) {
            c = (byte) in.readUnsignedByte();
            if (c == sequence[seqIndex]) {
                seqIndex++;
                if (seqIndex == sequence.length) {
                    return i + 1;
                }
            } else {
                seqIndex = 0;
            }
        }
        return -1;
    }

    private int getStartOfSequence(DataInputStream in, byte[] sequence) throws IOException {
        int end = getEndOfSeqeunce(in, sequence);
        return (end < 0) ? (-1) : (end - sequence.length);
    }

    private int parseContentLength(byte[] headerBytes, int length) throws IOException, NumberFormatException {
        int begin = findPattern(headerBytes, length, CONTENT_LENGTH_BYTES, 0);

        int end = findPattern(headerBytes, length, CONTENT_END_BYTES, begin) - CONTENT_END_BYTES.length;

        // converting string to int
        int number = 0;
        int radix = 1;
        for (int i = end - 1; i >= begin; --i) {
            if (headerBytes[i] > 47 && headerBytes[i] < 58) {
                number += (headerBytes[i] - 48) * radix;
                radix *= 10;
            }
        }

        return number;
    }

    private int findPattern(byte[] buffer, int bufferLen, byte[] pattern, int offset) {
        int seqIndex = 0;
        for (int i = offset; i < bufferLen; ++i) {
            if (buffer[i] == pattern[seqIndex]) {
                ++seqIndex;
                if (seqIndex == pattern.length) {
                    return i + 1;
                }
            } else {
                seqIndex = 0;
            }
        }

        return -1;
    }

    public Bitmap readMjpegFrame() throws IOException {
        mark(FRAME_MAX_LENGTH);
        int headerLen = getStartOfSequence(this, SOI_MARKER);


        if (headerLen < 0)
            return null;

        reset();

        readFully(gHeader, 0, headerLen);

        try {
            mContentLength = parseContentLength(gHeader, headerLen);
        } catch (NumberFormatException nfe) {
            nfe.getStackTrace();
            mContentLength = getEndOfSeqeunce(this, EOF_MARKER);
        }
        readFully(gFrameData, 0, mContentLength);
        Bitmap bm = BitmapFactory.decodeByteArray(gFrameData, 0, mContentLength, bitmapOptions);
        bitmapOptions.inBitmap = bm;

        return bm;
    }
}