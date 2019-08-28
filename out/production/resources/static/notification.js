function showNotification() {
            var error="";
            $.getJSON({ url: "/minimumBalanceNotification", type: 'GET', async: false},  function(data) {
            if (data.describe == "Data"){
                        error += "there are Materials with a balance of less than the minimum";}
              });

            $.getJSON({ url: "/minimumClassesNotification", type: 'GET', async: false},  function(data) {
                          if (data.describe == "Data"){
                                      error += "<br> there are Classes Material with a balance of less than the minimum";}
                            });


            if (error != "") {
                    $("#notification").html('<div class="alert alert-danger" role="alert">' + error + '</div>');
            }

            setTimeout(showNotification, 600000);
}

