$( document ).ready(function() {

    var status ;
    var yourturn=false;
    var sockjs ;

    function setConnected(){
        console.log('Info: WebSocket connection opened.');
        $('#enter').css('display','none');
        $('#game').css('display','block');
    }

    function parseJSON(str){
        return JSON.parse(str.replace(/\'/g,'\"'));
    }

    function cellSelected(){
        sockjs.send('click:'+ $(this).data('index'));
    }

    function enableClick(){
        $('#yourTable td').css('background','yellow')
        $('#yourTable td').on('click',cellSelected);
    }
    function disableClick(){
        $('#yourTable td').css('background','')
        $('#yourTable td').off('click',cellSelected);
    }

   $('#enjoy').click(function(){

        sockjs = new SockJS("http://localhost:8080/game");
       sockjs.onopen = function () {
        				setConnected(true);
        			};
       sockjs.onmessage = function (event) {
                        var msg = parseJSON(event.data);
        				console.log('Received: ' + event.data);

                        if(msg.command){
                            var command = msg.command;
                            if(command == "start"){
                                $('#started').css('display','block');
                            }else if(command == "yourturn"){
                                $('#turn_p').css('display','none');
                                $('#turn_y').css('display','block');
                                yourturn = true;
                                enableClick();
                            }else if(command == "nextturn"){
                                $('#turn_y').css('display','none');
                                $('#turn_p').css('display','block');
                                yourturn = false;
                                disableClick();
                            }else {
                                command = "you" + command;
                                $('#started').html("<strong>"+command+" </strong>");

                            }
                        }else if(msg.lastStatus){
                            var array = msg.lastStatus.split(',');
                            for(var i = 0 ; i < array.length ; i++){
                                $(".fixed td[data-index='"+i+"'] div").eq(0).html(array[i]);
                            }
                        }

        			};
       sockjs.onclose = function () {
        				//setConnected(false);
        				console.log('Info: WebSocket connection closed.');
        			};


   });
});