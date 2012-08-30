package org.sourceopen.hadoop.hive.resources.parser;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.hadoop.hive.ql.exec.FunctionInfo;
import org.apache.hadoop.hive.ql.exec.FunctionRegistry;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;

public class DWFunctionRegister {

    static {
        FunctionRegistry.registerTemporaryFunction("getvalue", com.taobao.hive.udf.logutil.UDFGetValue.class);
        FunctionRegistry.registerTemporaryFunction("checktag", com.taobao.hive.udf.UDFCheckTag.class);
        FunctionRegistry.registerTemporaryFunction("normalize", com.taobao.hive.udf.QueryNormalization.class);
        FunctionRegistry.registerTemporaryFunction("getvaluefromrefer", com.taobao.hive.udf.UDFGetValueFromRefer.class);
        FunctionRegistry.registerTemporaryFunction("concatEx", com.taobao.ad.data.search.udf.ConcatEx.class);
        FunctionRegistry.registerTemporaryFunction("coalesceStrEx", com.taobao.ad.data.search.udf.CoalesceStrEx.class);
        FunctionRegistry.registerTemporaryFunction("str_to_date", com.taobao.hive.udf.UDFStrToDate.class);
        FunctionRegistry.registerTemporaryFunction("getauctionid", com.taobao.hive.udf.UDFGetAuctionID.class);
        FunctionRegistry.registerTemporaryFunction("date_format", com.taobao.hive.udf.UDFDateFormat.class);
        FunctionRegistry.registerTemporaryFunction("BingDecode", com.taobao.hive.udf.UDFBingUrlDecode.class);
        FunctionRegistry.registerTemporaryFunction("row_number", com.taobao.ad.data.search.udf.UDFrow_number.class);
        FunctionRegistry.registerTemporaryFunction("date_add_taobao", com.taobao.hive.udf.UDFDateAdd2.class);
        FunctionRegistry.registerTemporaryFunction("getkeyvalue", com.taobao.hive.udf.UDFKeyValue.class);
        FunctionRegistry.registerTemporaryFunction("getnamefromrefer",
                                                   com.taobao.hive.udf.search.GetNameFromRefer.class);
        FunctionRegistry.registerTemporaryFunction("getnamefromurl", com.taobao.hive.udf.search.GetNameFromUrl.class);
        FunctionRegistry.registerTemporaryFunction("getvaluefrompre", com.taobao.hive.udf.search.GetValueFromPre.class);
        FunctionRegistry.registerTemporaryFunction("getvaluefromsplit",
                                                   com.taobao.hive.udf.search.GetValueFromSplit.class);
        FunctionRegistry.registerTemporaryFunction("ReplaceNullAndEmpty",
                                                   com.taobao.hive.udf.search.ReplaceNullAndEmpty.class);
        FunctionRegistry.registerTemporaryFunction("getcity", com.taobao.hive.udf.UDFGetCityByIp.class);
        FunctionRegistry.registerTemporaryFunction("trunc", com.taobao.hive.udf.UDFTrunc.class);
        FunctionRegistry.registerTemporaryFunction("explode_keyvalue", com.taobao.hive.udtf.UDTFExplodeKeyValue.class);
        FunctionRegistry.registerTemporaryFunction("date_sub_taobao", com.taobao.hive.udf.UDFDateSub2.class);
        FunctionRegistry.registerTemporaryFunction("date_diff", com.taobao.hive.udf.UDFDateDiff.class);
        FunctionRegistry.registerTemporaryFunction("url_decode", com.taobao.hive.udf.UDFURLDecode.class);
        FunctionRegistry.registerTemporaryFunction("get_cityid_byip", com.taobao.hive.udf.UDFGetIPCity.class);
        FunctionRegistry.registerTemporaryFunction("get_htmqian_n", com.taobao.hive.udf.UDFGetHtmQianN.class);
        FunctionRegistry.registerTemporaryFunction("codechinese", com.taobao.hive.udf.UDFUrlDecodeChinese.class);
        FunctionRegistry.registerTemporaryFunction("decodebase64", com.taobao.hive.udf.UDFDecodeBase64.class);
        FunctionRegistry.registerTemporaryFunction("getbase64string",
                                                   com.taobao.hive.udf.notcommon.UDFGetBase64String.class);
        FunctionRegistry.registerTemporaryFunction("udfstring", com.taobao.hive.udf.UDFString.class);
        FunctionRegistry.registerTemporaryFunction("encode", com.taobao.hive.udf.UDFEncode.class);
        FunctionRegistry.registerTemporaryFunction("timeCompare", com.taobao.hive.udf.UDFTimeCompare.class);
        FunctionRegistry.registerTemporaryFunction("encodebase64", com.taobao.hive.udf.UDFEncodeBase64.class);
        FunctionRegistry.registerTemporaryFunction("sysdate", com.taobao.hive.udf.UDFDateSysdate.class);
        FunctionRegistry.registerTemporaryFunction("idcard_info", com.taobao.hive.udf.UDFIDCard.class);
        FunctionRegistry.registerTemporaryFunction("get_keyvalue", com.taobao.hive.udf.UDFKeyValue.class);
        FunctionRegistry.registerTemporaryFunction("group_concat", com.taobao.hive.udf.UDAFGroupConcat2.class);
        FunctionRegistry.registerTemporaryFunction("getvaluefromurl", com.taobao.hive.udf.UDFGetValueFromUrl.class);
        FunctionRegistry.registerTemporaryFunction("getrealurl", com.taobao.hive.udf.UDFGetRealUrl.class);
        FunctionRegistry.registerTemporaryFunction("getpartition", com.taobao.hive.udf.UDFGetPartition.class);
        FunctionRegistry.registerTemporaryFunction("get_auctionid", com.taobao.hive.udf.UDFGetAuctionID2.class);
        FunctionRegistry.registerTemporaryFunction("num_format", com.taobao.hive.udf.UDFFormat.class);
        FunctionRegistry.registerTemporaryFunction("getDate", com.taobao.hive.udf.UDFGetDate.class);
        FunctionRegistry.registerTemporaryFunction("get_group_number", com.taobao.hive.udf.UDFGetGroupNumber.class);
        FunctionRegistry.registerTemporaryFunction("get_quarter_day", com.taobao.hive.udf.UDFGetQuarterDay.class);
        FunctionRegistry.registerTemporaryFunction("getweek", com.taobao.hive.udf.UDFGetWeek.class);
        FunctionRegistry.registerTemporaryFunction("getweekday", com.taobao.hive.udf.UDFGetWeekDay.class);
        FunctionRegistry.registerTemporaryFunction("ins", com.taobao.hive.udf.UDFIn.class);
        FunctionRegistry.registerTemporaryFunction("optionbit", com.taobao.hive.udf.UDFOptionBit.class);
        FunctionRegistry.registerTemporaryFunction("getReferHost", com.taobao.hive.udf.UDFReferHost.class);
        FunctionRegistry.registerTemporaryFunction("format_ip", com.taobao.hive.udf.UDFUserTbLastips.class);
        FunctionRegistry.registerTemporaryFunction("dateCompare", com.taobao.hive.udf.UDFDateCompare.class);
        FunctionRegistry.registerTemporaryFunction("base32Decoder", com.taobao.hive.udf.UDFBase32Decoder.class);
        FunctionRegistry.registerTemporaryFunction("getDomainFromUrl", com.taobao.hive.udf.UDFGetDomainFromUrl.class);
        FunctionRegistry.registerTemporaryFunction("getP4Pkeywords", com.taobao.hive.udf.UDFP4Pkeywords.class);
        FunctionRegistry.registerTemporaryFunction("exst_pt", com.taobao.hive.udf.UDFExstPartition.class);
        FunctionRegistry.registerTemporaryFunction("splitvalue", com.taobao.hive.search.udtf.UDTFSplitValue.class);
        // FunctionRegistry.registerTemporaryFunction("uniform", com.taobao.dw.common.udf.UniformUdf.class);
        // FunctionRegistry.registerTemporaryFunction("strjoinw", com.taobao.mrsstd.hiveudf.StrJoinW.class);
        // FunctionRegistry.registerTemporaryFunction("strjoin", com.taobao.mrsstd.hiveudf.StrJoin.class);
        // FunctionRegistry.registerTemporaryFunction("mergequery",
        // com.taobao.mrsstd.udaf.query_keywords.MergeQuery.class);
        // FunctionRegistry.registerTemporaryFunction("mergekeywords",
        // com.taobao.mrsstd.udaf.query_keywords.MergeKeywords.class);
        // FunctionRegistry.registerTemporaryFunction("subbits", com.taobao.mrsstd.hiveudf.SubBits.class);
        // FunctionRegistry.registerTemporaryFunction("strsimilarity", com.taobao.mrsstd.hiveudf.StrSimilarity.class);
        // FunctionRegistry.registerTemporaryFunction("hashcode", com.taobao.mrsstd.hiveudf.HashCode.class);
        // FunctionRegistry.registerTemporaryFunction("md5", com.taobao.mrsstd.hiveudf.Md5.class);
        // FunctionRegistry.registerTemporaryFunction("translate", com.alibaba.hive.udf.UDFTranslate.class);
        // FunctionRegistry.registerTemporaryFunction("instr", com.alibaba.hive.udf.UDFInstr.class);
        // FunctionRegistry.registerTemporaryFunction("GetCharType", com.alibaba.hive.udf.UDFGetCharType.class);
        // FunctionRegistry.registerTemporaryFunction("split_part", com.alibaba.hive.udf.UDFSplit_part.class);
        // FunctionRegistry.registerTemporaryFunction("dummy_string", com.alibaba.hive.udf.UDFDummy_string.class);
        // FunctionRegistry.registerTemporaryFunction("greatest", com.alibaba.hive.udf.UDFGreatest.class);
        // FunctionRegistry.registerTemporaryFunction("least", com.alibaba.hive.udf.UDFLeast.class);
        // FunctionRegistry.registerTemporaryFunction("sum_over", com.alibaba.hive.udf.UDFSum_over.class);
        // FunctionRegistry.registerTemporaryFunction("lag_over", com.alibaba.hive.udf.UDFLag_over.class);
        // FunctionRegistry.registerTemporaryFunction("first_value", com.alibaba.hive.udf.UDFFirst_value.class);
        // FunctionRegistry.registerTemporaryFunction("dense_rank", com.alibaba.hive.udf.UDFDense_rank.class);
        // FunctionRegistry.registerTemporaryFunction("row_rank", com.alibaba.hive.udf.UDFRow_rank.class);
        // FunctionRegistry.registerTemporaryFunction("b2b_row_number", com.alibaba.hive.udf.UDFRow_number.class);
        // FunctionRegistry.registerTemporaryFunction("justify_hours", com.alibaba.hive.udf.UDFJustify_hours.class);
        // FunctionRegistry.registerTemporaryFunction("B2BSum", com.alibaba.hive.udf.UDAFSum.class);
        // FunctionRegistry.registerTemporaryFunction("B2BAvg", com.alibaba.hive.udf.UDAFAvg.class);
        // FunctionRegistry.registerTemporaryFunction("split2rows", com.alibaba.hive.udf.UDTFSplit2rows.class);
        // FunctionRegistry.registerTemporaryFunction("b2b_md5", com.alibaba.hive.udf.UDFMd5.class);
        // FunctionRegistry.registerTemporaryFunction("add_month", com.alibaba.hive.udf.UDFAdd_Month.class);

    }

    public static FunctionInfo getFunctionInfo(String functionName) {
        return FunctionRegistry.getFunctionInfo(functionName);
    }

    public static <T> Method getMethodInternal(Class<? extends T> udfClass, String methodName, boolean exact,
                                               List<TypeInfo> argumentClasses) throws UDFArgumentException {
        return FunctionRegistry.getMethodInternal(udfClass, methodName, exact, argumentClasses);
    }

    public static Object invoke(Method m, Object thisObject, Object... arguments) throws HiveException {
        return FunctionRegistry.invoke(m, thisObject, arguments);
    }

}
