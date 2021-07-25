package io.mjmoore.traderepublic.partner.instrument;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mjmoore.traderepublic.instrument.Instrument;
import io.mjmoore.traderepublic.instrument.InstrumentEntity;
import io.mjmoore.traderepublic.instrument.InstrumentRepository;
import io.mjmoore.traderepublic.mappers.InstrumentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InstrumentServiceTest {

    private final InstrumentMapper mapper = new InstrumentMapper(new ObjectMapper());

    private final Instrument newInstrument
            = new Instrument(Instant.now(), "isin", "desc", InstrumentType.Add);
    private final Instrument deletedInstrument
            = new Instrument(Instant.now(), "isin", "desc", InstrumentType.Delete);

    @Mock private InstrumentRepository repository;

    private InstrumentService service;

    @BeforeEach
    public void setup() {
       service = new InstrumentService(repository, mapper);
    }

    @Test
    public void newInstrument() {
        service.accept(newInstrument);

        verify(repository, times(1)).save(any());
    }

    @Test
    public void addDuplicateInstrument() {
        when(repository.findByIsin(any()))
                .thenReturn(Optional.of(InstrumentEntity.builder().build()));

        service.accept(newInstrument);

        verify(repository, times(0)).save(any());
    }

    @Test
    public void deleteInstrument() {
        when(repository.findByIsin(any()))
                .thenReturn(Optional.of(InstrumentEntity.builder().build()));

        service.accept(deletedInstrument);

        verify(repository, times(1)).save(any());
    }

    @Test
    public void deleteNonExistentInstrument() {
        when(repository.findByIsin(any())).thenReturn(Optional.empty());

        service.accept(deletedInstrument);

        verify(repository, times(0)).save(any());
    }
}
