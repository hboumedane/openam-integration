$(document).ready ->
  welcome = (username) ->
    $("#login_form").hide()
    $("#registration_form").hide()
    welcomeText = $("<div>").text("Welcome back, #{username}!");
    welcomeText.addClass("welcome_text");
    $("#main_container").append(welcomeText)
    $(".authentication-required").removeClass("authentication-required")

  $.get($("#get-username-url").val()
  , (username) ->
    $("#main-progress").hide()
    if (username != "")
      welcome username
  )
  $("#login_form #username").focus()
  $("#login_form #username").focus()
  loginButton = $("#login_button")
  $("#login_form input").on "blur keyup keypress", (element) ->
    if $("#login_form input.validate.valid").length != $("#login_form input.validate").length
      if !loginButton.hasClass("disabled")
        loginButton.addClass("disabled")
    else
      loginButton.removeClass "disabled"
    null
  loginButton.click ->
    $.post($("#login-url").val(),
      {
        username: $("#login_form #username").val(),
        password: $("#login_form #password").val()
      }
    , (nothing) ->
      welcome($("#login_form #username").val())
    ).fail ->
      $("#login_form input").removeClass("valid")
      $("#login_form input").addClass("invalid")
      loginButton.addClass("disabled")
  registration_button = $("#registration_button");
  $("#registration_form input").on "blur keyup keypress", (element) ->
    if $("#registration_form input.validate.valid").length != $("#registration_form input.validate").length
      if !registration_button.hasClass("disabled")
        registration_button.addClass("disabled")
    else
      registration_button.removeClass "disabled"
    null
  registration_button.click ->
    $.post($("#registration-url").val(),
      {
        username: $("#registration_form #username").val(),
        password: $("#registration_form #password").val(),
        email: $("#registration_form #email").val(),
        fullName: $("#registration_form #full_name").val()
      }
    , (result) ->
      welcome $("#registration_form #username").val()
    ).fail ->
      $("#registration_form input").removeClass("valid")
      $("#registration_form input").addClass("invalid")
      loginButton.addClass("disabled")
