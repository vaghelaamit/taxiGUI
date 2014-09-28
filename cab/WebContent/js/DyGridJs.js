// JavaScript Document

$('document').ready(function(){
	
	/* Pop up Form */
	$('.create_row').click(function(){
		$('.createPopup_Bg').fadeIn(50);
		$('.createPopup').fadeIn(100);
	});
	$('.close_button').click(function(){
		$('.createPopup_Bg').fadeOut(300);
		$('.createPopup').fadeOut(500);
		$('.editPopup').fadeOut(500);
	});
	$('.createPopup_Bg').click(function(){
		$(this).fadeOut(300);
		$('.createPopup').fadeOut(500);
		$('.editPopup').fadeOut(100);
	});

	$('.edit_button').click(function(){
		$('.createPopup_Bg').fadeIn(50);
		$('.editPopup').fadeIn(100);
	});
	
	/* Table Button Click Events */
	
	$('.edit_button').click(function(){
		
	});
	$('.delete_button').click(function(){
		$(this).parent('[class^="DyGrid-td"]').parent('.table_row').remove();
	});
});

