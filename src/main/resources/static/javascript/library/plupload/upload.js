         
var flag = 0;

var uploadercont = new plupload.Uploader({
	browse_button: 'btnbrowse',
    runtimes: 'html5,flash',//只有html5和flash经过测试了
    url: '/UploadFile.ashx?type=3',
    chunk_size: '4mb',
    max_file_size: '200mb', 
    drop_element: 'uploader_filelist',    //

    max_retries: 0,            //当发生plupload.HTTP_E
    multi_selection: true,    //允许在文件浏览
    mutipart: true,

    filters: {
        prevent_duplicates: true,  //不允许
        mime_types: [
           { title: '文件类型', extensions: 'jpg,jpeg,gif,png,mpg,mpeg,avi,asf,rm,ram,mp4,flv,mp3,wav,wmv,rmi,mid,pdf,doc,docx,xls,xlsx,ppt,pptx,txt,html,htm' }
        ]
    },

    flash_swf_url: '/Script/plupload/Moxie.swf',  //

    preinit: {
        Init: function (up, info) {
            console.log(up.runtime);
            if( !(up.runtime == "flash" || up.runtime == "html5") ){
                $("#uploader").text("您的浏览器缺少 Flash或 HTML5 的支持");
            }
        },

        UploadFile: function(up, file) {
            var t = new Date().getTime();
            up.settings.multipart_params = { userID: "Justin", folderID: "3", runtime: up.runtime };

        }
    },

    init: {
        Refresh: function(up) {
        },

        BeforeUpload: function(up, file) {
        },

        StateChanged: function(up) {
        },

        QueueChanged: function(up) {
        },

        UploadProgress: function(up, file) {

            if (up.total.percent == 100) {  
                flag = flag + 1;

                if ((up.total.failed == 0) && (flag == 2))
                {                            
                    //layer.msg("上传资料成功了", { time: 2000 });                             
                    //setTimeout(aaa, 1000); 
                }
            }
        },

        FilesAdded: function(up, files) {

            plupload.each(files, function(file) {
                var ext = getFileExt(file.name);
                var size = file.size;
                var fileSizeState = checkFileSize(ext, size);
                if (fileSizeState == -1) {
                    up.removeFile(file);
                    layer.msg(file.name+'文件类型不支持', { time: 2000 });
                    onlyState = false;
                    return false;
                }
                else if (fileSizeState == 0) {
                    up.removeFile(file);
                    layer.msg(file.name+'文件太大', { time: 2000 });
                    onlyState = false;
                    return false;
                }
            });
        },

        FilesRemoved: function(up, files) {                    
            plupload.each(files, function(file) {

            });
        },

        FileUploaded: function (up, file, info) {
            up.settings.multipart_params = { folderID: "3", runtime: up.runtime, uploadFolder: "333" };
        },
        UploadComplete: function(up, file) {
            if (up.total.failed ==0) {
                layer.msg("上传资料成功了", { time: 2000 });
                setTimeout(closeLayer, 1000);
            }
            else
            {
                layer.msg("有"+ up.total.failed +"个资料上传失败了", { time: 2000 });
            }

        },

        ChunkUploaded: function(up, file, info) {                    

        },

        Error: function(up, args) {
            switch (args.code) {
                case -100: layer.msg('一般性错误', { time: 2000 }); break;
                case -200: layer.msg('网络错误', { time: 2000 }); break;
                case -300: layer.msg('文件不可读', { time: 2000 }); break;
                case -400: layer.msg('安全性太高，读取失败', { time: 2000 }); break;
                case -500: layer.msg('上传模块初始化出错', { time: 2000 }); break;
                case -600: layer.msg('文件太大', { time: 2000 }); break;
                case -601: layer.msg('文件类型不支持', { time: 2000 }); break;
                case -602: layer.msg('文件有重复', { time: 2000 }); break;
                case -700: layer.msg('图片格式错误', { time: 2000 }); break;
                case -701: layer.msg('内存发生错误', { time: 2000 }); break;
                case -702: layer.msg('错误：文件太大，超过了限定', { time: 2000 }); break;
            }
        }
    } 
});        
uploadercont.init();

var uploadercont1 = new plupload.Uploader({
    browse_button: 'btnbrowse1',
    runtimes: 'html5,flash',//只有html5和flash经过测试了
    url: '/UploadFile.ashx?type=3',
    chunk_size: '4mb',
    max_file_size: '200mb',
    drop_element: 'uploader_filelist',    //

    max_retries: 0,            //当发生plupload.HTTP_E
    multi_selection: true,    //允许在文件浏览
    mutipart: true,

    filters: {
        prevent_duplicates: true,  //不允许
        mime_types: [
           { title: '文件类型', extensions: 'jpg,jpeg,gif,png,mpg,mpeg,avi,asf,rm,ram,mp4,flv,mp3,wav,wmv,rmi,mid,pdf,doc,docx,xls,xlsx,ppt,pptx,txt,html,htm' }
        ]
    },

    flash_swf_url: '/Script/plupload/Moxie.swf',  //

    preinit: {
        Init: function (up, info) {
            console.log(up.runtime);
            if (!(up.runtime == "flash" || up.runtime == "html5")) {
                $("#uploader").text("您的浏览器缺少 Flash或 HTML5 的支持");
            }
        },

        UploadFile: function (up, file) {
            var t = new Date().getTime();
            up.settings.multipart_params = { userID: "Justin", folderID: "5", runtime: up.runtime };

        }
    },

    init: {
        Refresh: function (up) {
        },

        BeforeUpload: function (up, file) {
        },

        StateChanged: function (up) {
        },

        QueueChanged: function (up) {
        },

        UploadProgress: function (up, file) {

            if (up.total.percent == 100) {
                flag = flag + 1;

                if ((up.total.failed == 0) && (flag == 2)) {
                    //layer.msg("上传资料成功了", { time: 2000 });                             
                    //setTimeout(aaa, 1000); 
                }
            }
        },

        FilesAdded: function (up, files) {

            plupload.each(files, function (file) {
                var ext = getFileExt(file.name);
                var size = file.size;
                var fileSizeState = checkFileSize(ext, size);
                if (fileSizeState == -1) {
                    up.removeFile(file);
                    layer.msg(file.name + '文件类型不支持', { time: 2000 });
                    onlyState = false;
                    return false;
                }
                else if (fileSizeState == 0) {
                    up.removeFile(file);
                    layer.msg(file.name + '文件太大', { time: 2000 });
                    onlyState = false;
                    return false;
                }
            });
        },

        FilesRemoved: function (up, files) {
            plupload.each(files, function (file) {

            });
        },

        FileUploaded: function (up, file, info) {
            up.settings.multipart_params = { folderID: "3", runtime: up.runtime, uploadFolder: "333" };
        },
        UploadComplete: function (up, file) {
            if (up.total.failed == 0) {
                layer.msg("上传资料成功了", { time: 2000 });
                setTimeout(closeLayer, 1000);
            }
            else {
                layer.msg("有" + up.total.failed + "个资料上传失败了", { time: 2000 });
            }

        },

        ChunkUploaded: function (up, file, info) {

        },

        Error: function (up, args) {
            switch (args.code) {
                case -100: layer.msg('一般性错误', { time: 2000 }); break;
                case -200: layer.msg('网络错误', { time: 2000 }); break;
                case -300: layer.msg('文件不可读', { time: 2000 }); break;
                case -400: layer.msg('安全性太高，读取失败', { time: 2000 }); break;
                case -500: layer.msg('上传模块初始化出错', { time: 2000 }); break;
                case -600: layer.msg('文件太大', { time: 2000 }); break;
                case -601: layer.msg('文件类型不支持', { time: 2000 }); break;
                case -602: layer.msg('文件有重复', { time: 2000 }); break;
                case -700: layer.msg('图片格式错误', { time: 2000 }); break;
                case -701: layer.msg('内存发生错误', { time: 2000 }); break;
                case -702: layer.msg('错误：文件太大，超过了限定', { time: 2000 }); break;
            }
        }
    }
});
uploadercont1.init();

//$("#uploader").pluploadQueue({
//    runtimes: 'html5,flash',//只有html5和flash经过测试了
//    url: '/UploadFile.ashx?type=3',
//    chunk_size: '4mb',
//    max_file_size: '200mb', 
//    drop_element: 'uploader_filelist',    //
             
//    max_retries: 0,            //当发生plupload.HTTP_E
//    multi_selection: true,    //允许在文件浏览
//    mutipart: true,

//    filters: {
//        prevent_duplicates: true,  //不允许
//        mime_types: [
//           { title: '文件类型', extensions: 'jpg,jpeg,gif,png,mpg,mpeg,avi,asf,rm,ram,mp4,flv,mp3,wav,wmv,rmi,mid,pdf,doc,docx,xls,xlsx,ppt,pptx,txt,html,htm' }
//        ]
//    },

//    flash_swf_url: '/Script/plupload/Moxie.swf',  //
//    silverlight_xap_url: '/Script/plupload/Moxie.xap', //

//    preinit: {
//        Init: function (up, info) {
//            if( !(up.runtime == "flash" || up.runtime == "html5") ){
//                $("#uploader").text("您的浏览器缺少 Flash或 HTML5 的支持");
//            }
//        },

//        UploadFile: function(up, file) {
//            var t = new Date().getTime();
//            up.settings.multipart_params = { userID: "Justin", folderID: "3", runtime: up.runtime };

//        }
//    },

//    init: {
//        Refresh: function(up) {
//        },
         
//        BeforeUpload: function(up, file) {
//        },

//        StateChanged: function(up) {
//        },

//        QueueChanged: function(up) {
//        },

//        UploadProgress: function(up, file) {
                    
//            if (up.total.percent == 100) {  
//                flag = flag + 1;
                      
//                if ((up.total.failed == 0) && (flag == 2))
//                {                            
//                    //layer.msg("上传资料成功了", { time: 2000 });                             
//                    //setTimeout(aaa, 1000); 
//                }
//            }
//        },

//        FilesAdded: function(up, files) {
                    
//            plupload.each(files, function(file) {
//                var ext = getFileExt(file.name);
//                var size = file.size;
//                var fileSizeState = checkFileSize(ext, size);
//                if (fileSizeState == -1) {
//                    up.removeFile(file);
//                    layer.msg(file.name+'文件类型不支持', { time: 2000 });
//                    onlyState = false;
//                    return false;
//                }
//                else if (fileSizeState == 0) {
//                    up.removeFile(file);
//                    layer.msg(file.name+'文件太大', { time: 2000 });
//                    onlyState = false;
//                    return false;
//                }
//            });
//        },

//        FilesRemoved: function(up, files) {                    
//            plupload.each(files, function(file) {

//            });
//        },

//        FileUploaded: function (up, file, info) {
//            up.settings.multipart_params = { folderID: "3", runtime: up.runtime, uploadFolder: "333" };
//        },
//        UploadComplete: function(up, file) {
//            if (up.total.failed ==0) {
//                layer.msg("上传资料成功了", { time: 2000 });
//                setTimeout(closeLayer, 1000);
//            }
//            else
//            {
//                layer.msg("有"+ up.total.failed +"个资料上传失败了", { time: 2000 });
//            }
                    
//        },

//        ChunkUploaded: function(up, file, info) {                    

//        },

//        Error: function(up, args) {
//            switch (args.code) {
//                case -100: layer.msg('一般性错误', { time: 2000 }); break;
//                case -200: layer.msg('网络错误', { time: 2000 }); break;
//                case -300: layer.msg('文件不可读', { time: 2000 }); break;
//                case -400: layer.msg('安全性太高，读取失败', { time: 2000 }); break;
//                case -500: layer.msg('上传模块初始化出错', { time: 2000 }); break;
//                case -600: layer.msg('文件太大', { time: 2000 }); break;
//                case -601: layer.msg('文件类型不支持', { time: 2000 }); break;
//                case -602: layer.msg('文件有重复', { time: 2000 }); break;
//                case -700: layer.msg('图片格式错误', { time: 2000 }); break;
//                case -701: layer.msg('内存发生错误', { time: 2000 }); break;
//                case -702: layer.msg('错误：文件太大，超过了限定', { time: 2000 }); break;
//            }
//        }
//    } 
//});        

function closeLayer() {
    parent.layer.closeAll();
}
         
//判断文件后缀及大小
function checkFileSize(ext, size)
{
    var state = -1;//-1:格式不支持；0：文件大小超出范围；1：成功
    ext = (ext+"").toLowerCase();
    if (size > 200*1024*1024) {//200MB,视频最大，不允许超过
        return 0;
    }
    
    if (ext == "mpg" || ext == "mpeg" || ext == "asf" || ext == "avi" || ext == "wmv" || ext == "rm" || ext == "ram" || ext == "mp4" || ext == "flv") {
        if (size <= 200*1024*1024) {//200MB
            return 1;
        }
        else {
            return 0;
        }
    }    
    else if (ext == "pdf" || ext == "doc" || ext == "docx" || ext == "xls" || ext == "xlsx" || ext == "ppt" || ext == "pptx" || ext == "txt") {
        if (size <= 15*1024*1024) {//15MB
            return 1;
        }
        else {
            return 0;
        }
    }
    else if (ext == "jpg" || ext == "jpeg" || ext == "gif" || ext == "png") {
        if (size <= 1*1024*1024) {//1MB
            return 1;
        }
        else {
            return 0;
        }
    }
    else if (ext == "mp3" || ext == "wav" || ext == "lrc") {
        if (size <= 10*1024*1024) {//10MB
            return 1;
        }
        else {
            return 0;
        }
    }
    else if (ext == "html" || ext == "htm") {
        if (size <= 5*1024*1024) {//5MB
            return 1;
        }
        else {
            return 0;
        }
    }
    else {
        return -1;
    }    
}

//获取后缀,如MP3
function getFileExt(file) {
    try {
        var filename = file.replace(/.*(\/|\\)/, "");
        var fileExt = (/[.]/.exec(filename)) ? /[^.]+$/.exec(filename.toLowerCase()) : '';
        return fileExt;
    }
    catch (e) {
        return "";
    }
}
