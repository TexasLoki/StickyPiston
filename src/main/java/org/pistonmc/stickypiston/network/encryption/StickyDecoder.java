package org.pistonmc.stickypiston.network.encryption;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.pistonmc.Piston;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.GeneralSecurityException;
import java.util.List;

public class StickyDecoder extends MessageToMessageDecoder<ByteBuf> {

    private CryptBuf decodeBuf;

    public StickyDecoder(SecretKey sharedSecret, int capacity) {
        try {
            Cipher decode = Cipher.getInstance("AES/CFB8/NoPadding");
            decode.init(Cipher.DECRYPT_MODE, sharedSecret, new IvParameterSpec(sharedSecret.getEncoded()));
            this.decodeBuf = new CryptBuf(decode, capacity * 2);
        } catch (GeneralSecurityException e) {
            Piston.getLogger().severe(e);
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        decodeBuf.read(msg.array());
    }

}
