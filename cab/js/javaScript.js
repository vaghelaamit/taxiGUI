// JavaScript Document
$('document').ready(function(){
    /*  Top and Sidebar Tab */
	$('.topMenu [class^="tab-"] a').click(function(){
		topTabDeactive();
		$(this).addClass('active');
	});
	$('.sideMenu [class^="tab-"] a').click(function(){
		sideTabDeactive();
		$(this).addClass('active');
		$(this).addClass('left');
	});
	/*  Taxi Design  */
	$('.point-to-point').click(function(){
		$('[class^="taxi-"]').hide();
		$('.taxi-pop').fadeIn(300);
	});
	$('.airport').click(function(){
		$('[class^="taxi-"]').hide();
		$('.taxi-airport').fadeIn(300);
		$('.taxi-go_airport').fadeIn(300);
	});
	$('.package').click(function(){
		$('[class^="taxi-"]').hide();
		$('.taxi-package').fadeIn(300);
	});
	$('.outstation').click(function(){
		$('[class^="taxi-"]').hide();
		$('.taxi-outstation').fadeIn(300);
	});
	$('.car-on-rent').click(function(){
		$('[class^="taxi-"]').hide();
		$('.taxi-caronrent').fadeIn(300);
		$('.taxi-per_hours').fadeIn(300);
	});
	$('.taxi-ticket').click(function(){
		$('[class^="taxi-"]').hide();
		$('.taxi-taxiticket').fadeIn(300);
	});
	
	/* Airport Tab */
	$('.airport_tab [class^="tab"] a').click(function(){
		airportTabDeactive();
		$(this).addClass('active');
	});
	$('.airport_tab [class^="tab"] a.go_airport').click(function(){
		$('.taxi-pickup_airport').hide();
		$('.taxi-go_airport').fadeIn(300);
	});
	$('.airport_tab [class^="tab"] a.pick_airport').click(function(){
		$('.taxi-pickup_airport').fadeIn(300);
		$('.taxi-go_airport').hide();
	});
	
	/* Car on rent Tab*/
	$('.caronrent_tab [class^="tab"] a').click(function(){
		caronrentTabDeactive();
		$(this).addClass('active');
	});
	$('.caronrent_tab [class^="tab"] a.per_hours').click(function(){
		$('.taxi-day_wise').hide();
		$('.taxi-per_hours').fadeIn(300);
	});
	$('.caronrent_tab [class^="tab"] a.day_wise').click(function(){
		$('.taxi-day_wise').fadeIn(300);
		$('.taxi-per_hours').hide();
	});
	
});
function topTabDeactive(){
	$('.topMenu [class^="tab-"] a').removeClass('active');
}
function sideTabDeactive(){
	$('.sideMenu [class^="tab-"] a').removeClass('active');
	$('.sideMenu [class^="tab-"] a').removeClass('left');
}
function airportTabDeactive(){
	$('.airport_tab [class^="tab"] a').removeClass('active');
}
function caronrentTabDeactive(){
	$('.caronrent_tab [class^="tab"] a').removeClass('active');
}

/* Taxi outStation */
function addRow(){
	var addrow="";
	    addrow='<tr>';
        addrow+='<td><label>To :</label></td>';
        addrow+='<td><input type="text" placeholder="Pickup Area" required></td>';
        addrow+='<td><label class="fa fa-minus-square lm20 fa-lg"></label></td>';
        addrow+='</tr>';
		$('.addrow').append(addrow);
}