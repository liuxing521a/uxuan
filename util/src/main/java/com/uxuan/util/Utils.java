/*
 * Copyright [2015] [liuxing521a]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uxuan.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public final class Utils
{
	/**
	 * 线程休眠
	 * 
	 * @param timeMillis 要休眠时间
	 */
    public static void sleep(long timeMillis)
    {
        try
        {
            Thread.sleep(timeMillis);
        }
        catch(InterruptedException interruptedexception)
        {
            interruptedexception.printStackTrace();
        }
    }


    /**
     * md5加密
     * 
     * @param s 要加密字符串
     * @return 加密结果
     */
    public static String md5String(String s)
    {
        try
        {
            MessageDigest messagedigest = MessageDigest.getInstance("MD5");
            byte abyte0[] = s.getBytes();
            abyte0 = messagedigest.digest(abyte0);
            StringBuffer stringbuffer = new StringBuffer(32);
            for(int i = 0; i < abyte0.length; i++)
            {
                String s1 = Integer.toHexString(abyte0[i] & 0xff);
                if(s1.length() == 1)
                    stringbuffer.append('0');
                stringbuffer.append(s1);
            }

            return stringbuffer.toString();
        }
        catch(NoSuchAlgorithmException _ex)
        {
            return "";
        }
    }

    /**
     * 读取栈信息
     * 
     * @param throwable
     * @return
     */
    public static String getStackTrace(Throwable throwable)
    {
        if(throwable == null)
        {
            return "no exception";
        } else
        {
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
            PrintWriter printwriter = new PrintWriter(new OutputStreamWriter(bytearrayoutputstream));
            throwable.printStackTrace(printwriter);
            printwriter.flush();
            printwriter.close();
            return bytearrayoutputstream.toString();
        }
    }


    private Utils()
    {
    }

}

