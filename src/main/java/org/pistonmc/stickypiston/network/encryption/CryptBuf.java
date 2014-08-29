package org.pistonmc.stickypiston.network.encryption;

import org.pistonmc.Piston;

import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

public class CryptBuf {

    private final Cipher cipher;
    private final byte[] buffer;
    private int writePosition;
    private int readPosition;

    public CryptBuf(Cipher cipher, int bufSize) {
        this.cipher = cipher;
        this.buffer = new byte[bufSize];
    }

    public int read(byte[] dest) {
        if (readPosition >= writePosition) {
            return 0;
        } else {
            int amount = Math.min(dest.length, writePosition - readPosition);
            System.arraycopy(buffer, readPosition, dest, 0, amount);
            readPosition += amount;
            return amount;
        }
    }

    public void write(byte[] src, int length) {
        if (readPosition < writePosition) {
            throw new IllegalStateException("Stored data must be completely read before writing more data");
        }
        try {
            writePosition = cipher.update(src, 0, length, buffer, 0);
        } catch (ShortBufferException e) {
            Piston.getLogger().severe(e);
        }
        readPosition = 0;
    }

}
