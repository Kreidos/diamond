$.validator.setDefaults({
    highlight: function(element) {
        $(element).closest('.form-group').addClass('has-error');
    },
    unhighlight: function(element) {
        $(element).closest('.form-group').removeClass('has-error');
    },
    errorElement: 'span',
    errorClass: 'help-block',
    errorPlacement: function(error, element) {
    	if(element.attr("id") == "txtDomainName"){
    		error.insertAfter(element.parent().parent());
    	}else  if(element.parent('.input-group').length) {
            error.insertAfter(element.parent());
        }else{
        	error.insertAfter(element);
        }
    },
    submitHandler: function(form) { 
    	if(form.id == "frmLogin"){
			 $("#btnLogin").button('loading');
		}
    	var formtype = $(form).attr("form-type");
    	if("ajax" == formtype){
    		var container = $(form).attr("datatarget");
    		if(form.id == "frmShareDocument"){
   			 	$("#btnShare").button('loading');
    		}
    		$.ajax({
				type:"POST",
				url  : $(form).attr("action"),
				data : $(form).serialize(),
				success : function (response){
					$(container).html(response);
					if(form.id == "frmChangePassword" || form.id == "frmChangeUserPassword" ){
						$(form).each (function(){
							  this.reset();
						});
					}
					if($("#btnShare").length){
		    			$("#btnShare").button('reset');
		    		}
				}
			});
	    	return false;
    	}
    	form.submit();
    	if(form.id == "frmNewDocument" || form.id == "frmStartWorkflow" || form.id == "frmWFMTemplate" || form.id == "frmAttachments"  ){
			uploadProgress();
		}
	}
});

$.validator.addMethod('complexPassword', function(value, element) {
	return this.optional(element) || (value.match(/[a-z]/) && value.match(/[A-Z]/) && value.match(/[0-9]/) && value.match(/[\W_]/));
},'Password must contain 1 lower case ,1 upper case, 1 numeric and  special character.');


$.validator.addMethod('alpha', function(value, element){
	var regex = new RegExp("^[A-Za-z][A-Za-z _]*$");
    var key = value;
    if (!regex.test(key)) {
       return false;
    }
    return true;
},'Invalid input');

$.validator.addMethod('alphaSpace', function(value, element){
	var regex = new RegExp("^[A-Za-z][A-Za-z _]*(?:_[A-Za-z]+)*$");
    var key = value;
    if (!regex.test(key)) {
       return false;
    }
    return true;
},'Invalid input');

$.validator.addMethod('isDate', function(value, element){
	var isDate = true;
	//try{ $.datepicker.parseDate('dd-M-yy', value);   isDate = true;}catch (e){ alert(e);}
	return isDate;
});

$.validator.addMethod("time", function(value, element) {  
	return this.optional(element) || /^(([0-1]?[0-9])|([2][0-3])):([0-5]?[0-9])(:([0-5]?[0-9]))?$/i.test(value);  
}, "Invalid input");

$.validator.addMethod('isValidIP', function(value) { 
	var ip = /^(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[0-9]{2}|[0-9])(\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[0-9]{2}|[0-9])){3}$/;
    return value.match(ip);           
}, 'Invalid input');

$.validator.addMethod('checkExtension', function(value) {
	var extension = value.split('.').pop().toLowerCase();
	var actualExtension = $("#fileExtension").val().toLowerCase();
	return (extension == actualExtension);
});

$.validator.addMethod('requiredTime', function(value) {
	return !($('*[name=radType]')[0].checked && value=="");
});

$.validator.addMethod('requiredSMTP', function(value) {
	return !($('*[name=SMTPAuth]')[0].checked && value=="");
});

$.validator.addMethod('requiredStorage', function(value) {
	return !($('*[name=radStorageType]')[1].checked && value=="");
});

$.validator.addMethod("multiemail", function (value, element) {
    if (this.optional(element)) {
        return true;
    }
    var emails = value.split(','),
        valid = true;
 
    for (var i = 0, limit = emails.length; i < limit; i++) {
        value = jQuery.trim(emails[i])
        valid = valid && jQuery.validator.methods.email.call(this, value, element);
    }
    return valid;
}, "Please use a comma to separate multiple email addresses.");

$(document).ready(function() {
	$("#frmEditDocumentClassIndexes").validate({
		submitHandler : function(form) {
			var result = true;
			$(".indexOrder").each(function(){
				var currentIndexVlaue = $(this).val();
				var obj = $(this).closest("tr").next().find(".indexOrder");
				while(typeof obj.val() != "undefined"){
					if(currentIndexVlaue == obj.val()){
						BootstrapDialog.alert("Please select valid Index Order");
						result = false;
						return false;
					}else{
						obj = $(obj).closest("tr").next().find(".indexOrder");
					}
				}
			});
			if(result){
				form.submit();
			}
		}
	});
	$("#btnConfigure").click(function() {
	    var $btn = $(this);
	    $btn.button('loading');
	});
	
	$(document).on("submit", ".modalForm" , function(e){
		e.preventDefault();
		var validator = $(this).validate();  
		if($(this).valid()){
			var container = $(this).attr("datatarget");
			$.ajax({
				type : "POST",
				url  : $(this).attr("action"),
				data : $(this).serialize(),
				success : function (response){
					$(container).html(response);
					validator.resetForm();
				}
			});
	    	return false;
		}
	}); 
	
	$("#frmBulkAction").validate({
		errorElement: 'div',
	    errorContainer: "#errorWrapper",
	    errorLabelContainer:  '#errorDiv',
		errorClass : 'alert alert-danger',
	});
	
	$("#btnBulkDelete").click(function() {
	    if ($("#frmBulkAction").valid()) {
	    	BootstrapDialog.confirm("Are you sure, you want to delete these documents?", function(result){
	            if(result) {
	            	$("#frmBulkAction").submit();
	            }
	    	});
	    }
	});
	
	$("#btnBulkDownload").click(function() {
	    if ($("#frmBulkAction").valid()) {
        	$("#frmBulkAction").attr('action','/console/bulkdownload');
        	$("#frmBulkAction").submit();
	    }
	});
	
	$("#checkAll").change(function () {
	    $("input:checkbox").prop('checked', $(this).prop("checked"));
	});
	
	$('body').on('hidden.bs.modal', '.modal', function () {
		$(this).removeData('bs.modal');
	});
	
	$('.tip').tooltip();
	
	$("form").each(function () { 
		   var validator = $(this).validate();  //add var here
		   validator.resetForm();
		   $("#btnReset").click(function () {
		       validator.resetForm();
		   });
		   $("#btnResetForm").click(function () {
		       validator.resetForm();
		   });
		   $(".reset").click(function () {
		       validator.resetForm();
		   });
	});
	
	$(".internal").click(function(e) { //this function is used for tabs on view document view
		container=$(this).attr("datatarget");
		url = $(this).attr("data-src");
		$(container).html("<div class='text-center'><h3><i class='fa fa-spinner fa-spin'></i> Please wait...</h3></div>");
		$.ajax({
			type:"GET",
			url  : url,
			success : function (response){
				$(container).html(response);
				$(".viewdocument").colorbox({iframe:true,rel:"viewdocument", width:"100%", height:"100%", current : "Document {current} of {total}"});
			}
		});
	});
	
	$(document).on("click",".confirm", function(e) { //confirm delete for ajax and non ajax
		e.preventDefault();
		var targetUrl = $(this).attr("href");
		var title =  $(this).attr("title");
		var container = $(this).attr("datatarget");
		BootstrapDialog.confirm(title, function(result){
            if(result) {
            	if(container){
            		$(container).html("<div class='text-center'><h3><i class='fa fa-spinner fa-spin'></i> Please wait...</h3></div>");
    	        	$(container).load(targetUrl);
            	}else{
            		window.location.href=targetUrl;
            	}
            }
		});
	});
	$('.shortdate').datepicker({
		format: 'yyyy-mm-dd',
		clearBtn:true,
		todayHighlight:true,
		autoclose:true
	});
	
	$('#txtAnnouncementExpiryDate').datepicker("setStartDate",new Date());
	$('#txtExpiryDate').datepicker("setStartDate",new Date());
	
	$( ".autocomplete").typeahead({
		remote: {
			url: "",
			replace: function() {
				var $focused = $(':focus');
				var columnName = $focused.attr("id");
				var classId = $focused.attr("cid");
				var value = $focused.val();
				var myurl = "/console/autocomplete?&term="+encodeURIComponent(value)+"&indexname="+encodeURIComponent(columnName)+"&classid="+encodeURIComponent(classId);
				return myurl;
			}
		},
		limit:10,
		cache:false
	});
	
	$('.tt-hint').addClass('form-control');
	$(".autosubmit").bind("change",function(){
		var classid = $(this).val();
		window.location.href="/console/newdocument?classid="+classid;
	});
	
	$("#cmbIndexType").bind("change",function(){
		var selectedIndex = $('#cmbIndexType :selected').index();
		$("#txtIndexLength").prop('readonly',true);
		switch (selectedIndex){
		case 1 : 
			$("#txtIndexLength").val("10");
			break;
		case 2 : 
			$("#txtIndexLength").val("12");
			break;
		case 3 : 
			$("#txtIndexLength").val("50");
			break;
		case 4 : 
			$("#txtIndexLength").val("100");
			break;
		default:
			$("#txtIndexLength").prop('readonly',false);
				break;
		}
	});
	
	$(".operator").bind("change",function(){
		var selectedValue = $(this).val();
		var indexName = $(this).prop("name");
		var indexControlName =  indexName.substring(9); //removed the initial "operator_"
		$("#" + indexControlName + "_div").addClass("hidden");
		if(selectedValue == 7 ){
			$("#" + indexControlName + "_div").removeClass("hidden");
		}
	});
	
	$("#cmbIndexName").bind("change",function(){
		var indexName = $(this).val();
	    isAutoIndex = false;
	    $("#prefix").hide();
		$("#pattern").hide();
		$("#defaultvalue").show();
		
	    for(i = 0; i < autoIndexes.length; i++){
	        if(indexName == autoIndexes[i]){
	        	isAutoIndex = true;
	        	$("#prefix").show();
				$("#pattern").show();
				$("#defaultvalue").hide();
	            return;
	        }
	    }
	});
	
	$('input[name="radBindType"]').change( function(event) {
		$("#users").hide();
		$("#groups").hide();
		$("#roles").hide();
		if ($("#radBindType2").attr("checked")) {
			$("#users").show();
		}
		if ($("#radBindType3").attr("checked")) {
			$("#groups").show();
		}
		if ($("#radBindType4").attr("checked")) {
			$("#roles").show();
		}
	});
	
	$(".selectRow").bind("click", function(){
	    var selectType = $(this).val();
	    var check=eval("$('#cbRow_"+selectType+"').prop('checked')");
	    var elements = $(":input");
	    for(i=0; i<elements.length; i++)   {
	           var element = elements[i];
	           if((element.type == "checkbox") && (element.value == selectType) && (!element.disabled)) {
	                 element.checked =check;
	           }
	    }
	});
	
	$(".selectColumn").bind("click", function(){
	    var selectType = $(this).val();
	    var check=eval("$('#"+selectType+"Column').prop('checked')");
	    var elements = $(":input");
	    for(i=0; i<elements.length; i++)   {
	           var element = elements[i];
	           var val = element.name.indexOf(selectType);
	           if((element.type == "checkbox") && (val == 0) && (!element.disabled)) {
	                 element.checked =check;
	           }
	    }
	});
	$(".viewdocument").colorbox({iframe:true,rel:"viewdocument", width:"100%", height:"100%", current : "Document {current} of {total}", onClosed:function(){window.location.reload(true);}});
	
	$('#profilePic').click(function(){
		 $('input[type=file]').click();
	});

	$("#profilepicture").on("change", function() {
		 $("#profilepiccontent").html("<div class='text-center'><h3><i class='fa fa-spinner fa-spin'></i></div>");
		 $("#frmMyProfile").submit();
	});
	
	$('#dbPlatform').on('change', function() {
		$("#dbName").attr("readonly",true);
		 var portNo = "1527";
		 var dbOwner = "root";
		 if($(this).val() == "MYSQL"){
			 portNo = "3306";
			 dbOwner ="root";
		 }else if($(this).val() == "MSSQL"){
			 $("#dbName").attr("readonly",false);
			 portNo = "1433";
			 dbOwner ="sa";
		 }else if ($(this).val() == "ORACLE"){
			 $("#dbName").attr("readonly",false);
			 portNo = "1521";
			 dbOwner ="SYSTEM";
		 }else if ($(this).val() == "POSTGRE"){
			 portNo = "5432";
			 dbOwner ="postgres";
		 }else{
			 portNo = "1527";
			 dbOwner ="KRYSTALDBO";
		 }
		 $('#dbPort').val(portNo);
		 $('#dbOwner').val(dbOwner);
	});
	
	

});

/**AJAX File Upload Progress Function Starts **/
var req;
var myPercent = 0;
function uploadProgress(){
	var url = "/console/uploadprogress";
	if (window.XMLHttpRequest){ 
		req = new XMLHttpRequest();
		req.onreadystatechange = processStateChange;
		try{
			req.open("POST", url, true);
		} catch (e) {
			alert(e);
		}
		req.send(null);
	} else{
		req = new ActiveXObject("Microsoft.XMLHttp");
		if (req) {
			req.onreadystatechange = processStateChange;
			req.open("POST", url, true);
			req.send(null);
		}
	}
}

function processStateChange(){
	try{
		if (req.readyState == 4){
			if (req.status == 200){
				var xml = req.responseXML;
				if(xml != null){
					var isNotFinished = xml.getElementsByTagName("finished")[0];
					var myBytesRead = xml.getElementsByTagName("bytesRead")[0];
					var myContentLength = xml.getElementsByTagName("contentLength")[0];
					var myPercent = xml.getElementsByTagName("percentComplete")[0];
					var uploadError=xml.getElementsByTagName("uploadError")[0];
					$("#pbContainer").show();
					$("#progressMessage").html("");
					if(uploadError == null){
						if ((isNotFinished == null) && (myPercent == null)){
							window.setTimeout("uploadProgress()", 25);
						} else {
							myBytesRead = myBytesRead.firstChild.data;
							myContentLength = myContentLength.firstChild.data;
							if (myPercent != null) {
								myPercent = myPercent.firstChild.data;
								$("#progressMessage").html(myPercent + " % complete.");
								updateProgress(parseInt(myPercent));
								window.setTimeout("uploadProgress()", 25);
							} else {
								updateProgress(100);
								if($("#frmNewDocument").length){
									$("#progressMessage").html("Document added successfully"); 
									$("#frmNewDocument").each (function(){
										  this.reset();
									});
								}
								if($("#frmWFMTemplate").length){
									$("#progressMessage").html("Workflow template added successfully"); 
									$("#frmWFMTemplate").each (function(){
										  this.reset();
									});
								}
								if($("#frmAttachments").length){
									$("#progressMessage").html("Attachment added successfully"); 
									$("#frmAttachments").each (function(){
										  this.reset();
									});
									url = $(".internal").attr("data-src");
									$("#resultAttachments").load(url);
								}
								if($("#frmStartWorkflow").length){
									$("#progressMessage").html("Workflow case started successfully"); 
									$("#frmStartWorkflow").each (function(){
										  this.reset();
									});
								}
							}
						}
					}else{
						$("#progressMessage").html("<span style='color:red;'>"+uploadError.firstChild.data +"</span>"); 
						if($("#frmNewDocument").length){
							$("#frmNewDocument").each (function(){
								  this.reset();
							});
						}
						if($("#frmAttachments").length){
							$("#frmAttachments").each (function(){
								  this.reset();
							});
						}
						if($("#frmStartWorkflow").length){
							$("#progressMessage").html("Workflow case started successfully"); 
							$("#frmStartWorkflow").each (function(){
								  this.reset();
							});
						}
					}
				}else{
					window.setTimeout("uploadProgress()", 25);
				}
			} else {
				alert(req.statusText);
			}
		}
	} catch (e) {
		alert(e);
	}
}

function updateProgress(myPercent){
	$(document).ready(function(){
		$("#progressbar").css("width", myPercent+"%");
		if(myPercent >= 100){
			$('#progressbarMain').fadeOut(1000);
		}else{
			$("#progressbarMain").show();
		}
	});
}