package ru.netology

import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class NoteServiceTest {

    @Before
    fun prepareData() {
        val firstId = NoteService.add("заметка 1", "VK социальная сеть для всех")
        val secondId = NoteService.add("заметка 2", "VK the best", false)
        val commId = NoteService.createComment(1, "и для каждого")
        val oneMoreCommId = NoteService.createComment(1, "FB рулит")

    }

    @After
    fun deleteData() {
        NoteService.clearAll()
    }

    @Test
    fun add_one_note() {
        val id = NoteService.add("заметка 1", "VK социальная сеть для всех")
        assertTrue(id > 0)
    }

    fun add_second_note() {
        val id = NoteService.add("заметка 2", "VK the best", false)
        assertTrue(id > 0)
    }

    @Test
    fun create_first_comment() {
        val id = NoteService.createComment(1, "и для каждого")
        assertTrue(id > 0)
    }

    @Test(expected = CantCommentException::class)
    fun create_unappropriate_comment() {
        val id = NoteService.createComment(2, "FB рулит")
    }

    @Test
    fun delete_note() {
        assertTrue(NoteService.delete(2))
    }

    @Test
    fun delete_one_comment() {
        assertTrue(NoteService.deleteComment(1))
    }

    @Test
    fun restore_first_comment() {
        assertTrue(NoteService.restoreComment(1))
    }

    @Test
    fun edit_first_note() {
        assertTrue(NoteService.edit(1, "заметка 1- исправление", "VK социальная сеть для всех людей"))
    }

    @Test
    fun edit_comment() {
        assertTrue(NoteService.editComment(1, "Nice!"))
    }

    @Test(expected = CommentNotFoundException::class)
    fun edit_nonexisting_comment() {
        assertTrue(NoteService.editComment(100, "Too bad:("))
    }

    @Test(expected = AccessToDeletedCommentException::class)
    fun edit_deleted_comment() {
        assertTrue(NoteService.deleteComment(2))
        assertTrue(NoteService.editComment(2, "Let's try!"))
    }

    @Test
    fun get_list_of_notes() {
        val listNotes = NoteService.get("1", 1, 1)
        assertTrue(listNotes.isEmpty())
    }

    @Test
    fun get_right_notes_by_id() {
        val listNotes = NoteService.get("1", 0, 1)
        assertTrue(listNotes.isNotEmpty())
    }

    @Test(expected = NoteNotFoundException::class)
    fun get_by_nonexisting_id() {

        NoteService.getById(1000)
    }

    @Test
    fun get_comment_to_first_note() {
        val commentToNote = NoteService.getComments(1, 0, 1)
        assertTrue(commentToNote.isNotEmpty())
    }

    @Test
    fun get_wrong_amount_of_comments() {
        val commentToNote = NoteService.getComments(1, 10, 10)
        assertTrue(commentToNote.isEmpty())
    }

}