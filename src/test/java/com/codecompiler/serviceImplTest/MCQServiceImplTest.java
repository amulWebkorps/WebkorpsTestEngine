package com.codecompiler.serviceImplTest;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.codecompiler.entity.MCQ;
import com.codecompiler.repository.MCQRepository;
import com.codecompiler.service.impl.MCQServiceImpl;

public class MCQServiceImplTest {
	 	@Mock
	    private MCQRepository mcqRepository;
	 
	 	@InjectMocks 
	    private MCQServiceImpl serviceImpl;

	    @BeforeEach
	    public void setUp() {
	        MockitoAnnotations.initMocks(this);
	       
	    }


	    @Test
	    public void testDeleteMcqWhenAlreadyDeleted() {
	        // Arrange
	        String mcqId = "123";
	        MCQ mcq = new MCQ();
	        mcq.setMcqId(mcqId);
	        mcq.setMcqStatus(false); // Set mcqStatus to false
	        when(mcqRepository.findByMcqId(mcqId)).thenReturn(Optional.of(mcq).get());

	        // Act
	        MCQ deletedMcq = serviceImpl.deleteMcq(mcqId);

	        // Assert
	        verify(mcqRepository).findByMcqId(mcqId); // Verify that findByMcqId was called
	        verify(mcqRepository, never()).save(deletedMcq); // Verify that save was not called
	        assertNull(deletedMcq); // Verify that null is returned since the MCQ is already deleted
	    }

	    @Test
	    public void testDeleteMcqWhenNotExists() {
	        // Arrange
	        String mcqId =anyString();
	        when(mcqRepository.findByMcqId(mcqId)).thenReturn(null);

	        // Act
	        MCQ deletedMcq = serviceImpl.deleteMcq(mcqId);

	        // Assert
	        verify(mcqRepository).findByMcqId(mcqId); // Verify that findByMcqId was called
	        verify(mcqRepository, never()).save(Mockito.any()); // Verify that save was not called
	        assertNull(deletedMcq); // Verify that null is returned when MCQ does not exist
	    }
	
}
