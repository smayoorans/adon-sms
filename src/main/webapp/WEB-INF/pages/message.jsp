<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.6/css/bootstrap.min.css"
          integrity="sha384-rwoIResjU2yc3z8GV/NPeZWAv56rSmLldC3R/AZzGRnGxQQKnKkoFVhFQhNUwEyJ" crossorigin="anonymous">

    <link href="<c:url value="/resources/signin.css"/>" rel="stylesheet">
</head>
<body>

<div class="container">


    <form class="form-signin" name="form" method="post" action="send-message">
        <h2 class="form-signin-heading">Adon Message Box</h2>

        <br>
        <c:if test="${not empty msg}">
            <div class="alert alert-warning alert-dismissible fade show" role="alert">
                    ${msg}
                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
        </c:if>
        <label for="message" class="">Enter a Message to Send all Subscribers</label>
        <textarea rows="3" id="message" name="message" class="form-control" style="resize: none; overflow: hidden"
                  autocomplete="false" required></textarea>
        <br>
        <button class="btn btn-lg btn-primary btn-block" type="submit" style="font-family: inherit">Send</button>

        <br>
        <span>
            <a href="subscribe" class="btn btn-sm btn-success">Subscribe</a>
            <a href="unsubscribe" class="btn btn-sm btn-warning float-right">Un-Subscribe</a>
        </span>
    </form>

</div> <!-- /container -->

<!-- jQuery first, then Tether, then Bootstrap JS. -->
<script src="https://code.jquery.com/jquery-3.1.1.slim.min.js"
        integrity="sha384-A7FZj7v+d/sdmMqp/nOQwliLvUsJfDHW+k9Omg/a/EheAdgtzNs3hpfag6Ed950n"
        crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/tether/1.4.0/js/tether.min.js"
        integrity="sha384-DztdAPBWPRXSA/3eYEEUWrWCy7G5KFbe8fFjk5JAIxUYHKkDx6Qin1DkWx51bBrb"
        crossorigin="anonymous"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.6/js/bootstrap.min.js"
        integrity="sha384-vBWWzlZJ8ea9aCX4pEW3rVHjgjt7zpkNpZk+02D9phzyeVkE+jo0ieGizqPLForn"
        crossorigin="anonymous"></script>


<%--<script type="text/javascript">
    $("#alert-warning").alert();
    window.setTimeout(function() { $("#alert-warning").alert('close'); }, 2000);
</script>--%>


</body>
</html>

