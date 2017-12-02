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


    <form class="form-signin" name="form" method="post" action="verify_pin_action">
        <h2 class="form-signin-heading">AdOn Subscription</h2>

        <br>
        <div class="alert alert-warning alert-dismissible fade show" role="alert">
            ${msg}
        </div>

        <input type="hidden" id="msisdn" name="msisdn" value="${msisdn}">
        <input type="hidden" id="serverRef" name="serverRef" value="${serverRef}">

        <label for="pin" class="">Please Enter Your PIN</label>
        <input type="text" id="pin" name="pin" class="form-control" placeholder="Enter the PIN"
               autocomplete="false" required autofocus>
        <br>
        <button class="btn btn-lg btn-primary btn-block" type="submit" style="font-family: inherit">Verify & Register
        </button>
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
</body>
</html>
