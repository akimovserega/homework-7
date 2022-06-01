package ru.netology

import java.lang.Integer.min

fun main() {

}

data class Note(
    val id: Int = 0,
    val title: String = "",
    val text: String = "",
    val comments: Int = 0,
    val canComment: Boolean = true,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Note
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id
    }

    override fun toString(): String {
        return "$title $text"
    }
}

data class Comment(
    val id: Int = 0,
    val noteId: Int = 0,
    val message: String = "",
    val deleted: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Comment
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id
    }

    override fun toString(): String {
        return message
    }
}

// исключения
class NoteNotFoundException(message: String) : RuntimeException(message)
class CommentNotFoundException(message: String) : RuntimeException(message)
class CantCommentException(message: String) : RuntimeException(message)

// ошибка доступа к удаленному комментарию
class AccessToDeletedCommentException(message: String) : RuntimeException(message)


object NoteService {

    //методы без разграничения прав доступа

    private var notes = mutableListOf<Note>()
    private var comments = mutableListOf<Comment>()

    private var nextIdNote = 0
    private var nextIdComment = 0

    fun clearAll(): Boolean {
        nextIdNote = 0
        nextIdComment = 0
        notes = mutableListOf<Note>()
        comments = mutableListOf<Comment>()
        return true
    }

    // добавить заметку, без параметрв privacy, commentPrivacy, privacyView, privacyComment. доп параметр canComment
    fun add(title: String, text: String, canComment: Boolean = true): Int {
        nextIdNote++
        notes += Note(nextIdNote, title, text, 0, canComment)
        return nextIdNote
    }

    // создать комментарий, без параметров ownerId, replyTo, guId
    fun createComment(noteId: Int, message: String): Int {
        val findNote = getById(noteId)
        if (!findNote.canComment) {
            throw CantCommentException("Нельзя комментировать данную заметку")
        } else {
            nextIdComment++
            comments += Comment(nextIdComment, noteId, message)

            val commentsNumber = findNote.comments
            notes[notes.indexOf(findNote)] = findNote.copy(comments = commentsNumber + 1)
        }
        return nextIdComment
    }

    //удалить заметку, вместе с комментариями
    fun delete(noteId: Int): Boolean {
        val findNote = getById(noteId)
        // удаление всех комментариев
        if (findNote.comments > 0) {
            comments.removeIf { it.noteId == noteId }
        }


        return notes.remove(findNote)
    }

    //удалить комментарий, без параметра ownerId
    fun deleteComment(commentId: Int): Boolean {
        val indexOfComment = comments.indexOf(Comment(commentId))
        if (indexOfComment >= 0) {
            comments[indexOfComment] = comments[indexOfComment].copy(deleted = true)
        } else {
            throw CommentNotFoundException("Комментарий не найден")
        }
        return true
    }

    // редактировать заметку, без параметров privacy, commentPrivacy, privacyView, privacyComment
    fun edit(noteId: Int, title: String, text: String): Boolean {
        val findNote = getById(noteId)
        notes[notes.indexOf(findNote)] = findNote.copy(title = title, text = text)
        return true
    }


    //редактировать комментарий, без параметра ownerId
    fun editComment(commentId: Int, message: String): Boolean {
        val indexOfComment = comments.indexOf(Comment(commentId))
        if (indexOfComment >= 0) {
            if (!comments[indexOfComment].deleted) {
                comments[indexOfComment] = comments[indexOfComment].copy(message = message)
            } else {
                throw  AccessToDeletedCommentException("Комментарий удален, редактирование невозможно")
            }
        } else {
            throw CommentNotFoundException("Комментарий не найден")
        }
        return true
    }

    //вернуть список заметок, без параметров userId, sort
    fun get(noteIds: String, offset: Int = 0, count: Int): List<Note> {
        val findNotes = emptyList<Note>().toMutableList()
        val indexArray = noteIds.split(" ")

        if (indexArray.isNotEmpty() && offset < indexArray.size && count > 0) {
            for (i in offset until min(offset + count, indexArray.size)) {
                findNotes += getById(indexArray[i].toInt())
            }
        }
        return findNotes
    }

    // вернуть заметку по id, без параметров: owner_id, need_wiki
    fun getById(noteId: Int): Note {
        val indexOfNote = notes.indexOf(Note(noteId))
        // поиск заметки
        if (indexOfNote >= 0) {
            return notes[indexOfNote]
        } else {
            throw NoteNotFoundException("заметка не найдена")
        }
    }

    // вернуть массив комментариев к заметке, без параметра sort
    fun getComments(noteId: Int, offset: Int = 0, count: Int): List<Comment> {
        var returnComments = emptyList<Comment>()
        val findNote = getById(noteId)
        if (findNote.comments > 0 && count > 0) {
            // отфильтровать комментарии, удаленные не возвращать
            val findComments = comments.filter { it.noteId == noteId && !it.deleted }
            if (findComments.isNotEmpty() && offset < findComments.size) {
                val toInd =
                    if (count + offset - 1 <= findComments.lastIndex) count + offset - 1 else findComments.lastIndex
                returnComments = findComments.subList(offset, toInd + 1)
            }
        }
        return returnComments
    }

    // восстановить удаленный комментарий
    fun restoreComment(commentId: Int): Boolean {
        val indexOfComment = comments.indexOf(Comment(commentId))
        if (indexOfComment >= 0) {
            if (comments[indexOfComment].deleted) {
                comments[indexOfComment] = comments[indexOfComment].copy(deleted = false)
            }
        } else {
            throw CommentNotFoundException("Комментарий не найден")
        }
        return true
    }
}

