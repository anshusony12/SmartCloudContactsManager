console.log("hi");

const toggleSidebar = ()=>{
    if($(".sidebar").is(":visible")){
    	
        $(".sidebar").css("display", "none");
        $(".content").css("margin-left", "0%");
        
    }
    else{
        $(".sidebar").css("display", "block");
        $(".content").css("margin-left", "20%");
    }
}

const search=()=>{
    let query = $("#search-content").val();
    // console.log(query);
    if(query == ''){
        $(".search-result").hide();
        $(".nofound").hide();
    }
    else{
      
       let url = `http://localhost:8080/search/${query}`;
       
       fetch(url).then((response)=>{
            return response.json();
       }).then((data)=> {
           console.log(data);
            let text = `<div class="list-group">`;
            data.forEach(contact => {
                text+= `<a href="/user/${contact.cId}/contact" class="list-group-item list-group-item-action">${contact.name}</a>`;
            });
            
            text+= `</div>`;
            if(data.length > 0){
                 $(".nofound").hide();
                $(".search-result").html(text);
                $(".search-result").show();
            }else{
            	$(".search-result").hide();
        		$(".nofound").show();
            }
            
               
       });
    }
};

<!-- eye icon function -->
$('.toggle-password').on('click', function() {
  $(this).toggleClass('fa-eye fa-eye-slash');
  let input = $($(this).attr('toggle'));
  if (input.attr('type') == 'password') {
    input.attr('type', 'text');
  }
  else {
    input.attr('type', 'password');
  }
});


// first request to server to create order 
const paymentStart=()=>{
  console.log("payment started")
  let amount = $("#payment_field").val()
  console.log(amount)
  if(amount=='' || amount == null){
    swal({
      title: "Null",
      text: "amount is required",
      icon: "error",
      button: "OK!",
    });
    return ;
  }
  // code to request oder
  // we will use ajax to send request to create order 
  $.ajax({
    url:'/user/create_order',
    data:JSON.stringify({amount:amount,info:'order_request'}),
    contentType:'application/json',
    type:'POST',
    dataType:'json',
    success:function(response){
      //invoked when success
      console.log(response)
      if(response.status =='created'){
        // open payment form
        var options = {
          "key": "rzp_test_uR1cQnxrG3jram", 
          "amount": response.amount, 
          "currency": "INR",
          "name": "Smart Contact Manager",
          "description": "Donation",
          "image": "https://anshusony12.github.io/images/profile.jpg",
          "order_id": response.id, 
          "handler": function (response){
            console.log(response.razorpay_payment_id)
            console.log(response.razorpay_order_id)
            console.log(response.razorpay_signature)
            updateOrder(response.razorpay_payment_id, response.razorpay_order_id, 'paid');
            console.log("payment successfull...!!")
            
          },
          "prefill": {
              "name": "",
              "email": "",
              "contact": ""
          },
          "notes": {
              "address": "LearnCode with Anshu Sony"
          },
          "theme": {
              "color": "#3399cc"
          }
      };
        var rzp1 = new Razorpay(options); 
        rzp1.on('payment.failed', function (response){ 
          console.log(response.error.code); 
          console.log(response.error.description); 
          console.log(response.error.source); 
          console.log(response.error.step); 
          console.log(response.error.reason); 
          console.log(response.error.metadata.order_id); 
          console.log(response.error.metadata.payment_id); 
          // alert("oops !! payment failed..")
          swal({
            title: "OOPS !!",
            text: "Payment Failed",
            icon: "error",
            button: "OK!",
          });
        });
        rzp1.open();
      }
    },
    error:function(error){
      //invoked when error
      console.log(error)
      alert("something went wrong !!")
    }
  });
};

function updateOrder(payment_id, order_id, status){
console.log("update order is called");
  $.ajax({
    url:'/user/update_order',
    data:JSON.stringify({payment_id:payment_id, order_id:order_id, status:status}),
    contentType:'application/json',
    type:'POST',
    dataType:'json',
    success:function(response){
      swal({
        title: "Congrats !!",
        text: "Payment Successfull",
        icon: "success",
        button: "OK!",
      });
    },
    error:function(error){
      swal({
        title: "OOPS !!",
        text: "Payment Successfull, but our server didn't get payment details yet, we will notify to you as soon as possible through email later!!",
        icon: "error",
        button: "OK!",
      });
    }
  })
}