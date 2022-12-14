//Script here
//var json = eval("("+timeJson+")");
/**
 *对象判空
 */

// var timeJson = "{\"message\": \"success\", \"ncov_city_data\": {\"420600\": {\"updateTime\": 1582256741, \"cityCode\": \"420600\", \"cityName\": \"襄阳\"}}}";
//  var districtcode = "420600";

function isEmptyArray(a) {

    if ((typeof a) == 'object' && a != null && a.length > 0) {
        return false;
    } else {
        return true;
    }
}

function printObject(a) {
    var r = null;
    if (null !== a && typeof(a) != "undefined" && (typeof a) == 'object') {
        var array = Object.getOwnPropertyNames(a);
        if (!isEmptyArray(array)) {
            for (var index in array) {
                var n = array[index];
                var o = array[n];
                r += "|" + n;
            }
            Alert(r);
        }
    }
}

function pad(str) {
    return +str >= 10 ? str : '0' + str
}

function timestampToTime(timestamp) {
    var dateObj = new Date(+timestamp) // ps, 必须是数字类型，不能是字符串, +运算符把字符串转化为数字，更兼容
    var year = dateObj.getFullYear() // 获取年，
    var month = dateObj.getMonth() + 1 // 获取月，必须要加1，因为月份是从0开始计算的
    var date = dateObj.getDate() // 获取日，记得区分getDay()方法是获取星期几的。
    var hours = pad(dateObj.getHours()) // 获取时, pad函数用来补0
    var minutes = pad(dateObj.getMinutes()) // 获取分
    var seconds = pad(dateObj.getSeconds()) // 获取秒
    return year + '-' + month + '-' + date + ' ' + hours + ':' + minutes + ':' + seconds
}


var json = JSON.parse(timeJson);
var ncov_city_data = json["ncov_city_data"];
var district = null;
var updateTime = null;
//province
if (null !== json && null != ncov_city_data && typeof(ncov_city_data) != "undefined") {


    district = ncov_city_data[districtcode];

    if (null != district && typeof(district) != "undefined") {

        printObject(district);
        var updateTime = district["updateTime"];
        // alert(updateTime)
        // if (null !== tt) {
        //     updateTime = timestampToTime(tt);
        //     alert(updateTime)
        // }
    }
}