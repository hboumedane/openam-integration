$(document).ready ->
  main_progress = $("#main-progress");
  fake_image_url = "https://morganparadis.files.wordpress.com/2014/08/9674878_orig.jpg"
  image = $("#image")
  $('.materialboxed').materialbox()
  $.get($("#profile-url").val(), (profile) ->
    main_progress.hide()
    $("#image-preloader").removeClass "active"
    console.log(profile)
    $("#username").val(profile.username)
    $("#email").val(profile.email)
    $("#full_name").val(profile.full_name)
    $("input").focus()
    $("input").focus()
    if (profile.image_url == null)
      image.attr("src", fake_image_url)
    else
      image.attr("src", profile.image_url)
      fake_image_url = profile.image_url
  ).fail (error) ->
    if (error.status == 401)
      window.location = $("#logout-url").val()
  save_button = $("#save_profile_button");
  $("input").on "blur keyup keypress", ->
    if $("input.validate.valid").length != $("input.validate").length
      if !save_button.hasClass("disabled")
        save_button.addClass("disabled")
    else
      save_button.removeClass "disabled"
    null
  image_input = $("#image-input");
  image_input.on "change paste keyup", ->
    if (image_input.val() == "")
      image.attr("src", fake_image_url)
    else
      image.attr("src", image_input.val())
  save_button.click ->
    update_data = {
      username: $("#username").val(),
      password: $("#password").val(),
      email: $("#email").val(),
      fullName: $("#full_name").val()
    }
    if (image_input.val() != "")
      update_data["imageUrl"] = image_input.val()

    main_progress.show()
    $.post($("#update-profile-url").val(), update_data,
      (done) ->
        main_progress.hide()
        if (done == 'true')
          toast('Profile was saved!', 3000, 'rounded')
        else
          toast('Something went wrong, sorry about that.', 3000, 'rounded')
    )
  message = $("#message").val()
  if (message != "")
    toast(message, 5000, 'rounded')