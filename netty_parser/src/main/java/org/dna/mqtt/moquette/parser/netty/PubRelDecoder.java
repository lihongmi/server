/*
 * Copyright (c) 2012-2014 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */
package org.dna.mqtt.moquette.parser.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.util.AttributeMap;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.dna.mqtt.moquette.proto.messages.AbstractMessage;
import org.dna.mqtt.moquette.proto.messages.MessageIDMessage;
import org.dna.mqtt.moquette.proto.messages.PubRelMessage;

/**
 *
 * @author andrea
 */
class PubRelDecoder extends DemuxDecoder {
    
    @Override
    void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws UnsupportedEncodingException {
        decode((AttributeMap)ctx, in, out);
    }

//    @Override
    void decode(AttributeMap ctx, ByteBuf in, List<Object> out) throws UnsupportedEncodingException {
        in.resetReaderIndex();
        //Common decoding part
        MessageIDMessage message = new PubRelMessage();
        if (!decodeCommonHeader(message, in)) {
            in.resetReaderIndex();
            return;
        }
        
        //if 3.1.1, check the flags (dup, retain and qos == 0)
        if (message.isDupFlag() || message.isRetainFlag() || message.getQos() != AbstractMessage.QOSType.LEAST_ONE) {
            throw new CorruptedFrameException("Received a PURREL with fixed header flags != b0010");
        }
        
        //read  messageIDs
        message.setMessageID(in.readUnsignedShort());
        out.add(message);
    }

}

