console.log("bye");

const toggleContent=()=>{
	if($('.sidebar').is(":visible"))
	{
		$('.sidebar').css("display","none");
		$('.con').css("margin-left","0%");
	}
	else
	{
		$('.sidebar').css("display","block");
		$('.con').css("margin-left","20%");
	}
};


function deleteContact(cid,page)
	{
			swal({
		  title: "Are you sure?",
		  text: "You Want to delete this contact...!",
		  icon: "warning",
		  buttons: true,
		  dangerMode: true,
		})
		.then((willDelete) => {
		  if (willDelete) {

			window.location="/user/delete/"+cid+"/"+page;

		  } else {
		    swal("Your contact is safe!");
		  }
	});
	
	}
	
	
	const search=()=>
	{
		
		
		let keyword=$("#search").val();
		if(keyword=='')
		{ 
			$(".search-result").hide();
			
			
		}
		else{
			console.log("jop");
			console.log(keyword);
			
			let url=`http://localhost:8181/search-contact/${keyword}`;
			
			fetch(url).then((response)=>
			{
				return response.json();
			})
			.then((data)=>{
				console.log(data);
				
				if(data.length!=0)
				{
					
					let text=`<div class="list-group">`;
				
				
				data.forEach((contact)=>{
					
					text+=`<a href="/user/contact/${contact.cId}"  class="list-group-item list-group-action text-uppercase" >${contact.name}</a>`;
					
				});
				
				
				text+=`</div>`;
				
				$(".search-result").html(text);
				$(".search-result").show();
				}
				
				else
				{
					let newText=`<p class="text-uppercase text-danger">contact not found!!</p>`;
					$(".search-result").html(newText);
					$(".search-result").show();

				}
				

			});
		}
		
		
		
		};
		