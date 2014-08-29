package org.pistonmc.stickypiston.network.encryption;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.pistonmc.Piston;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.GeneralSecurityException;
import java.util.List;

public class StickyEncoder extends MessageToMessageEncoder<ByteBuf> {

    private CryptBuf encodeBuf;

    public StickyEncoder(SecretKey sharedSecret, int capacity) {
        try {
            Cipher encode = Cipher.getInstance("AES/CFB8/NoPadding");
            encode.init(Cipher.ENCRYPT_MODE, sharedSecret, new IvParameterSpec(sharedSecret.getEncoded()));
            this.encodeBuf = new CryptBuf(encode, capacity * 2);
        } catch (GeneralSecurityException e) {
            Piston.getLogger().severe(e);
        }
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        encodeBuf.read(msg.array());
    }

}
