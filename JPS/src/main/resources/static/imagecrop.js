var canvas_width = 800;
var canvas_height = 1000;
var jcrop_api;
var canvas;
var context;
var image;

var snapcanvas;



var prefsize;

var filetype;
var filename;

var maxwidth = $("#maxwidth").val();
var maxheight = $("#maxheight").val();

$("#profilephotofile").change(function() {
  loadImage(this);
  $("#crophelp").show();
  $("#cropbutton").show();
});

function loadImage(input) {
  if (input.files && input.files[0]) {
	  filetype=input.files[0].type;
	  filename = input.files[0].name;
    var reader = new FileReader();
    canvas = null;
    
    reader.onload = function(e) {
      image = new Image();
      image.onload = validateImage;
      image.src = e.target.result;
    }
    reader.readAsDataURL(input.files[0]);
    
//    alert(filetype);
  }
}

function dataURLtoBlob(dataURL) {
  var BASE64_MARKER = ';base64,';
  if (dataURL.indexOf(BASE64_MARKER) == -1) {
    var parts = dataURL.split(',');
    var contentType = parts[0].split(':')[1];
    var raw = decodeURIComponent(parts[1]);

    return new Blob([raw], {
      type: contentType
    });
  }
  var parts = dataURL.split(BASE64_MARKER);
  var contentType = parts[0].split(':')[1];
  var raw = window.atob(parts[1]);
  var rawLength = raw.length;
  var uInt8Array = new Uint8Array(rawLength);
  for (var i = 0; i < rawLength; ++i) {
    uInt8Array[i] = raw.charCodeAt(i);
  }

  return new Blob([uInt8Array], {
    type: contentType
  });

}

function validateImage() {
  if (canvas != null) {
    image = new Image();
    image.onload = restartJcrop;
    image.src = canvas.toDataURL(filetype);
  } else restartJcrop();
}



function restartJcrop() {
  if (jcrop_api != null) {
    jcrop_api.destroy();
  }
  $("#views").empty();
  $("#views").append("<canvas id=\"canvas\">");
  canvas = $("#canvas")[0];
  context = canvas.getContext("2d");
  ar = image.width/image.height;

  var newWidth = canvas.width;
  var newHeight = newWidth / ar;
  if (newHeight > canvas_height) {
      newHeight = canvas_height;
      newWidth = newHeight * ar;
  }
  
  console.log("new height="+newHeight);
  console.log("new width="+newWidth);
  
  canvas.width = newWidth;
  canvas.height = newHeight;
  
//  context.drawImage(image, 0, 0);
  context.drawImage(image, 0, 0, image.width,    image.height,     // source rectangle
          0, 0, canvas.width, canvas.height); // destination rectangle
  
  $("#canvas").Jcrop({
    onSelect: selectcanvas,
    onRelease: clearcanvas,
    boxWidth: canvas.width,
    boxHeight: canvas.height,
    setSelect: [0,0,maxwidth,maxheight],
    allowResize: false,
    minSize: [ maxwidth, maxheight ],
    maxSize: [ maxwidth, maxheight ]
  }, function() {
    jcrop_api = this;
  });
  clearcanvas();
}

function clearcanvas() {
  prefsize = {
    x: 0,
    y: 0,
    w: canvas.width,
    h: canvas.height,
  };
}

function selectcanvas(coords) {
  prefsize = {
    x: Math.round(coords.x),
    y: Math.round(coords.y),
    w: Math.round(coords.w),
    h: Math.round(coords.h)
  };
}

function applyCrop() {
  var w = prefsize.w;
  var h = prefsize.h; 
  
  var origimage = new Image();
  origimage.src = canvas.toDataURL(filetype);
  
  $("#views").empty();
  $("#views").append("<canvas id=\"croppedcanvas\">");
  
  canvas = $("#croppedcanvas")[0];
  canvas.width = w;
  canvas.height = h;
  
  context = canvas.getContext("2d");
  
  if (origimage.complete)
	   context.drawImage(origimage, prefsize.x, prefsize.y, prefsize.w, prefsize.h, 0, 0, canvas.width, canvas.height);
  else
	  origimage.onload = function () {
	  context.drawImage(origimage, prefsize.x, prefsize.y, prefsize.w, prefsize.h, 0, 0, canvas.width, canvas.height);
  }
  
}

function applyScale(scale) {
  if (scale == 1) return;
  canvas.width = canvas.width * scale;
  canvas.height = canvas.height * scale;
  context.drawImage(image, 0, 0, canvas.width, canvas.height);
  validateImage();
}


$("#cropbutton").click(function(e) {
  applyCrop();
  $("#cropUpload").show();
  $("#cropbutton").hide();
  
  $("#profileform").find('input:file').val('');
  
});
$("#scalebutton").click(function(e) {
  var scale = prompt("Scale Factor:", "1");
  applyScale(scale);
});


$("#profileform").submit(function(e) {
  e.preventDefault();
  var formAction = $(this).attr("action");

  formData = new FormData($(this)[0]);
  
  var id = $(this).find('input#id').val();
  
  console.log(id);
  
  formData.append("id",id);
  
  console.log(formAction);
  
  var blob = dataURLtoBlob(canvas.toDataURL(filetype));
  //---Add file blob to the form data

  formData.append("croppedfile", blob,filename);
  
  var xhr = new XMLHttpRequest();
  
  $.ajax({
    url: formAction,
    type: "POST",
    data: formData,
    contentType: false,
    enctype: 'multipart/form-data',
    cache: false,
    processData: false,
    xhr: function() {
        return xhr;
   },
   success: function(response) {
	  // alert('You are now at URL: ' + xhr.responseURL);
//	   window.location.href = xhr.responseURL;    	
    },
    error: function(data) {
      
      alert("Error");
    },
    complete: function(data) {
    	window.location.href = xhr.responseURL;
    }
  });
});

$("#captureimage").click(function(e) {
	
	Webcam.set({
		  width: maxwidth,
		  height: maxheight,
		  image_format: 'jpeg',
		  jpeg_quality: 90
		 });
		 Webcam.attach( '#snapshot' );
	
});
$("#takesnapshot").click(function(e) {
	Webcam.snap( function(data_uri) {
		snapcanvas = null;
		
		var snapimage = new Image();
	    snapimage.src = data_uri;
	    
		$("#snapshot").empty();
		 $("#snapshot").append("<canvas id=\"snapcanvas\">");
		  snapcanvas = $("#snapcanvas")[0];
		  snapcontext = snapcanvas.getContext("2d");
		  
		  snapcanvas.width = maxwidth;
		  snapcanvas.height = maxheight;	
		  
		  if (snapimage.complete)
			  snapcontext.drawImage(snapimage, 0,0);
		  else
			  snapimage.onload = function () {
			  snapcontext.drawImage(snapimage, 0,0);
		  }
		  
		  $("#snapUpload").show();
		  $("#takesnapshot").hide();
 } );
});
$("#profileformsnapshot").submit(function(e) {
	  e.preventDefault();
	  formData = new FormData($(this)[0]);
	  var blob = dataURLtoBlob(snapcanvas.toDataURL('image/jpg'));
	  //---Add file blob to the form data

	  formData.append("croppedfile", blob,"snapshot.jpg");
	  
	  var xhr = new XMLHttpRequest();
	  
	  $.ajax({
	    url: "changeprofile",
	    type: "POST",
	    data: formData,
	    contentType: false,
	    enctype: 'multipart/form-data',
	    cache: false,
	    processData: false,
	    xhr: function() {
	        return xhr;
	   },
	   success: function(response) {
		  // alert('You are now at URL: ' + xhr.responseURL);
//		   window.location.href = xhr.responseURL;    	
	    },
	    error: function(data) {
	      alert("Error");
	    },
	    complete: function(data) {
	    	window.location.href = xhr.responseURL;
	    }
	  });
	});
//<script type="text/javascript">
//function camera() {
//	 
//	 Webcam.set({
//		  width: 200,
//		  height: 200,
//		  image_format: 'jpeg',
//		  jpeg_quality: 90
//		 });
//		 Webcam.attach( '#my_camera' );
//		 
//	 
//}
//
//
//<!-- Code to handle taking the snapshot and displaying it locally
//function take_snapshot() {
//
//// take snapshot and get image data
//Webcam.snap( function(data_uri) {
// // display results in page
// document.getElementById('results').innerHTML = 
// '<img src="'+data_uri+'"/>';
// } );
//}
//</script> 


