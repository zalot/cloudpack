package com.taobao.hive.udf;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
import java.util.*;

public class UDFUrlDecodeChinese extends UDF {
    
    private Text res = new Text();
    //private static URLDecoder_Str = new URLDecoder();
    private Object UnsupportedEncodingException;
    
    
    
    
    public UDFUrlDecodeChinese() {
       
    }
    
    private String urlencode_return(String splitUrlString,String encodeValue) throws UnsupportedEncodingException
    {
       return URLDecoder.decode(splitUrlString, encodeValue);
       
    }
    
    
    private String url_decode_code(String url_encode) throws UnsupportedEncodingException
    {
       if (url_encode == null)
       {
           return null;      
       }
       
       //String value = ;
       
       StringBuffer StrValue = new StringBuffer("");
       StringBuffer StrUrlCode = new StringBuffer("");
       StringBuffer StrCharCode = new StringBuffer("");
       //StrValue.
       //String[] StrPlit = url_encode.split(split_char);
         
           int m = url_encode.length();
           int n = 0;
           while(n<m)
           {
              if(url_encode.charAt(n) == "%".charAt(0))
              {
                if(StrCharCode.length() > 0 )
                {
                  StrValue.append(toChinese(StrCharCode.toString(),"gb2312"));
                  StrCharCode.delete(0, StrCharCode.length());
                }
                //if((int i = url_encode.substring(n,n+3).split("%").length) == 2)
                //{
              StrUrlCode.append(url_encode.substring(n,n+3));
              n+=3;
               // }
               // else
                //{
                  //  StrValue.append(url_encode.substring(n,n+1));
                  //  n=n+1;
               // }
              }
              else if(url_encode.charAt(n) == "\\".charAt(0))
              {
                  if(StrUrlCode.length() > 0 )
                  {
                     StrValue.append(urlencode_return(StrUrlCode.toString(),"gb2312"));
                     StrUrlCode.delete(0, StrUrlCode.length());
                  }
                  
                  //StrValue.append(urlencode_return(StrCharCode.toString(),"gb2312"));
                  if(m-n>=2 &&  url_encode.substring(n,n+2).equals("\\x"))
                  {
                  StrCharCode.append(url_encode.substring(n,n+4).replace("\\x", ""));
                                   n+=4;
                  }
                  else
                  {
                     StrValue.append(toChinese(StrCharCode.toString(),"gb2312"));
                     StrCharCode.delete(0, StrValue.length());
                     StrValue.append(url_encode.substring(n,n+1));
                     n=n+1;
                  }
              }
              else 
              {
                  if(url_encode.indexOf("\\", n) < url_encode.indexOf("%", n) )
                  {
                     
                     if(StrCharCode.length() > 0 )
                     {
                         StrValue.append(toChinese(StrCharCode.toString(),"gb2312"));
                         StrCharCode.delete(0, StrValue.length());
                     }
                     if(StrUrlCode.length() > 0 )
                     {
                         StrValue.append(urlencode_return(StrUrlCode.toString(),"gb2312"));
                         StrUrlCode.delete(0, StrUrlCode.length());
                     }
                     if(url_encode.indexOf("\\", n) > 0)
                     {
                     StrValue.append(url_encode.substring(n, url_encode.indexOf("\\", n)));
                     n = url_encode.indexOf("\\", n);
                     }
                     else
                     {
                         StrValue.append(url_encode.substring(n, url_encode.indexOf("%", n)));
                         n = url_encode.indexOf("%", n);
                     }
                      
                     
                  }
                  else if(url_encode.indexOf("\\", n) > url_encode.indexOf("%", n) )
                  {
                     
                     if(StrCharCode.length() > 0 )
                     {
                         StrValue.append(toChinese(StrCharCode.toString(),"gb2312"));
                         StrCharCode.delete(0, StrValue.length());
                     }
                     if(StrUrlCode.length() > 0 )
                     {
                         StrValue.append(urlencode_return(StrUrlCode.toString(),"gb2312"));
                         StrUrlCode.delete(0, StrUrlCode.length());
                     }
                     if(url_encode.indexOf("%", n) > 0)
                     {
                     StrValue.append(url_encode.substring(n, url_encode.indexOf("%", n)));
                     n = url_encode.indexOf("%", n);
                     }
                     else
                     {
                         StrValue.append(url_encode.substring(n, url_encode.indexOf("\\", n)));
                         n = url_encode.indexOf("\\", n);
                     }
                     //n = url_encode.indexOf("%", n);
                  }
                  else if(url_encode.indexOf("\\",n) < 0 && url_encode.indexOf("%",n)< 0)
                  {
                     StrValue.append(url_encode);
                     n=m;
                  }
                  //else()
              }
           }
                     
           
           
       
       
       if(StrCharCode.length() > 0 )
       {
           StrValue.append(toChinese(StrCharCode.toString(),"gb2312"));
           StrCharCode.delete(0, StrCharCode.length());
       }
       if(StrUrlCode.length() > 0 )
       {
           StrValue.append(urlencode_return(StrUrlCode.toString(),"gb2312"));
           StrUrlCode.delete(0, StrUrlCode.length());
       }
        
       return StrValue.toString();
       
       //return toChinese(StrValue.toString(),"gb2312");
       
    }
    
    
    public String url_decode(String url,String encodeStr){
          if (url == null)
            return new String();

          int len = url.length();
          String str = new String(url);
          String out = new String();
          int i = 0;
          String tmp = new String();
          while(i < len)
          {
            try{
              tmp = str.substring(i, i+1);
              if(tmp.equals("+"))
                out += " ";
              else if((i<len-1)&&str.substring(i, i+2).equals("\\x"))
              {
                String tmp1 = str.substring(i+2, i+4);
                if(i+6>=len-1)
                  break;
                if(str.substring(i+4, i+6).equals("\\x"))
                {
                  tmp1 += str.substring(i+6, i+8);
                }
                else
                {
                  i += 4;
                  continue;
                }
                try{
                  out += toChinese(tmp1, encodeStr);
                  i += 8;
                continue;
                }catch(UnsupportedEncodingException e){System.out.println("UnsupportedEncodingException");}
              }
              else
                out += tmp;
            }catch(Exception e)
            {e.printStackTrace();
             System.out.println(url);}
            i ++;
          }
          return out.trim().replace("%20", " ");
        }
    
    private StringBuffer toChinese(String asc, String encodingString) throws UnsupportedEncodingException{
        int len = asc.length() / 2;
        byte[] bytes = new byte[len];
        for(int i = 0; i < len; i ++)
        {
          String code = asc.substring(i*2, (i+1)*2);
          bytes[i] = Integer.valueOf(code, 16).byteValue();
        }
        String chi = new String(bytes, encodingString);
        StringBuffer StrChi = new StringBuffer(chi);
        return StrChi;
      }

    public Text evaluate(Text StringCode) throws UnsupportedEncodingException
    {
       if (StringCode == null )
       {
           return null;
       }
        try{
       String translate_val = URLDecoder.decode(StringCode.toString().replace("\\x", "%"),"gbk");
       res.set(translate_val);
        }
        catch(Exception e)
        {
        
        return null;
        }


       return res;
           
    }
    
    /*public static void main(String[] args) //throws UnsupportedEncodingException
    {
       Text codestr = new Text("\\xd7\\xca\\xd6\\xce\\xcd\\xa8\\xbc\\xf8     \\xb0\\xd8\\xd1\\xee");
       //int xx = codestr.toString().
       //System.out.print(xx);
       //String Str = "d7a8b9f1d5fdc6b7";
       //int lens = Str.length()/2;
       //System.out.print(StrReturn.toString());
       //byte[] bytes = new byte[lens];
       //for(int i = 0; i < lens; i ++)
        //{
        //  String code = Str.substring(i*2, (i+1)*2);
        //  bytes[i] = Integer.valueOf(code, 16).byteValue();
        //}
       //String var = 

       try
       {
       Text codeStrReturn = evaluate(codestr);
       System.out.print(codeStrReturn.toString());
       //String test = URLDecoder.decode(codestr.toString(), "gb2312");
       //System.out.print(test);
       }
       catch(UnsupportedEncodingException e)
       {
           
       }
       
       
    }*/
       public static void main(String args[]){
    	   UDFUrlDecodeChinese test = new UDFUrlDecodeChinese();
    	   try {
			System.out.println(test.evaluate(new Text("525ON028r154GmE866033643w658O7")));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       }

}




