$(document).ready ->
  main_progress = $("#main-progress");
  $.get $("#list-users-url").val(), (users) ->
    main_progress.hide()
    if users.length > 0
      $(".flow-text").hide()
      console.log(users)
      for user in users
        user_card = $("<div>")
        user_card.addClass "row col s12 card"

        if (user.image_url != null && user.image_url != "")
          card_image = $("<div>").addClass "card-image"
          img_element = $("<img>").attr "src", user.image_url
          img_element.addClass "materialboxed"
          card_image.append img_element

          title = $("<span>").addClass("card-title white-text");
          title.text("Username: #{user.username}.")
          card_image.append title
          user_card.append(card_image)


        card_content = $("<div>")
        card_content.addClass "card-content"

        if (user.image_url == null || user.image_url == "")
          card_title = $("<span>")
          card_title.addClass "card-title";
          card_title.text("Username: #{user.username}.")
          card_content.append card_title

        card_text = $("<p>")
        card_text.addClass "black-text flow-text"
        card_text.text("Full name: #{user.full_name}.")
        card_content.append card_text

        card_text = $("<p>")
        card_text.addClass "black-text flow-text"
        card_text.text("Email: #{user.email}.")
        card_content.append card_text

        card_actions = $("<div>")
        card_actions.addClass "card-action"

        action_write = $("<a>")
        action_write.text("Write message")
        card_actions.append action_write

        action_ban = $("<a>")
        action_ban.text("Ban this user")
        card_actions.append action_ban

        card_content.append card_actions
        user_card.append card_content
        $("#users-here").append(user_card)
      $('.materialboxed').materialbox()