# Сервис "Заметки" NoteService
Из класса Note исключены поля: owner_id, date, read_comments, view_url, privacy_view, text_wiki

Из класса Comment исключены поля: user_id, owner_id, date, reply_to

Из методов NoteService исключены параметры:
- в методе add отсутствуют: privacy, comment_privacy, privacy_view, privacy_comment, добавлен параметр can_comment;
- в методе createComment отсутствуют: owner_id, reply_to, gu_id;
- в методе deleteComment отсутствует owner_id;
- в методе edit отсутствует: privacy, comment_privacy, privacy_view, privacy_comment;
- в методе editComment отсутствует: owner_id;
- в методе get отсутствуют: user_id, sort;
- в методе getById отсутствуют: owner_id, need_wiki;
- в методе getComments отсутствует sort.
