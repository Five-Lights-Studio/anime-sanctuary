// package com.fls.animecommunity.animesanctuary.controller;

// import com.fls.animecommunity.animesanctuary.controller.html.PageController;
// import com.fls.animecommunity.animesanctuary.model.note.dto.NoteResponseDto;
// import com.fls.animecommunity.animesanctuary.service.interfaces.NoteService;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.setup.MockMvcBuilders;

// import static org.mockito.Mockito.when;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

// class PageControllerTest {

//     @Mock
//     private NoteService noteService;

//     @InjectMocks
//     private PageController pageController;

//     private MockMvc mockMvc;

//     @BeforeEach
//     void setup() {
//         MockitoAnnotations.openMocks(this);
//         mockMvc = MockMvcBuilders.standaloneSetup(pageController).build();
//     }

//     @Test
//     void testList() throws Exception {
//         mockMvc.perform(get("/topic/list"))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("list"));
//     }

//     @Test
//     void testGetPost() throws Exception {
//         Long noteId = 1L;
//         NoteResponseDto noteResponseDto = new NoteResponseDto(); // 가짜 데이터를 설정합니다.
//         when(noteService.getNote(noteId)).thenReturn(noteResponseDto);

//         mockMvc.perform(get("/topic/read/{note_id}", noteId))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("read"))
//                 .andExpect(model().attributeExists("post"));
//     }

//     @Test
//     void testWrite() throws Exception {
//         mockMvc.perform(get("/topic/write"))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("write"));
//     }

//     @Test
//     void testUpdate() throws Exception {
//         mockMvc.perform(get("/topic/update/{note_id}", 1L))
//                 .andExpect(status().isOk())
//                 .andExpect(view().name("update"));
//     }
// }
