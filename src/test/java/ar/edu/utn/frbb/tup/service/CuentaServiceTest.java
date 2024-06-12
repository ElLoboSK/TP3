package ar.edu.utn.frbb.tup.service;

import ar.edu.utn.frbb.tup.model.*;
import ar.edu.utn.frbb.tup.model.exception.CuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.model.exception.TipoCuentaAlreadyExistsException;
import ar.edu.utn.frbb.tup.persistence.CuentaDao;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CuentaServiceTest {

    @Mock
    private CuentaDao cuentaDao;

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private CuentaService cuentaService;


    @BeforeAll
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCuentaAlreadyExistsException() throws CuentaAlreadyExistsException, TipoCuentaAlreadyExistsException {
        Cliente pepeRino = new Cliente();
        pepeRino.setDni(26456437);
        pepeRino.setNombre("Pepe");
        pepeRino.setApellido("Rino");
        pepeRino.setFechaNacimiento(LocalDate.of(1978, 3,25));
        pepeRino.setTipoPersona(TipoPersona.PERSONA_FISICA);

        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(123456789);
        cuenta.setBalance(10000);
        cuenta.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE);
        cuenta.setMoneda(TipoMoneda.PESOS);

        when(cuentaDao.find(cuenta.getNumeroCuenta())).thenReturn(cuenta);
        assertThrows(CuentaAlreadyExistsException.class, () -> cuentaService.darDeAltaCuenta(cuenta,pepeRino.getDni()));
    }

    @Test
    public void testCuentaSuccess() throws CuentaAlreadyExistsException, TipoCuentaAlreadyExistsException {
        Cliente pepeRino = new Cliente();
        pepeRino.setDni(26456437);
        pepeRino.setNombre("Pepe");
        pepeRino.setApellido("Rino");
        pepeRino.setFechaNacimiento(LocalDate.of(1978, 3,25));
        pepeRino.setTipoPersona(TipoPersona.PERSONA_FISICA);

        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(123456789);
        cuenta.setBalance(10000);
        cuenta.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE);
        cuenta.setMoneda(TipoMoneda.PESOS);

        cuentaService.darDeAltaCuenta(cuenta,pepeRino.getDni());
        verify(cuentaDao, times(1)).save(cuenta);
    }

    @Test
    public void testCuentaNoSoportada() throws CuentaAlreadyExistsException, TipoCuentaAlreadyExistsException {
        Cliente pepeRino = new Cliente();
        pepeRino.setDni(26456437);
        pepeRino.setNombre("Pepe");
        pepeRino.setApellido("Rino");
        pepeRino.setFechaNacimiento(LocalDate.of(1978, 3,25));
        pepeRino.setTipoPersona(TipoPersona.PERSONA_FISICA);

        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(123456789);
        cuenta.setBalance(10000);
        cuenta.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE);
        cuenta.setMoneda(TipoMoneda.DOLARES);

        assertThrows(IllegalArgumentException.class, () -> cuentaService.darDeAltaCuenta(cuenta,pepeRino.getDni()));
    }

    @Test
    public void testTipoCuentaAlreadyExistsException() throws CuentaAlreadyExistsException ,TipoCuentaAlreadyExistsException {
        Cliente pepeRino = new Cliente();
        pepeRino.setDni(26456437);
        pepeRino.setNombre("Pepe");
        pepeRino.setApellido("Rino");
        pepeRino.setFechaNacimiento(LocalDate.of(1978, 3,25));
        pepeRino.setTipoPersona(TipoPersona.PERSONA_FISICA);

        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(123456789);
        cuenta.setBalance(10000);
        cuenta.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE);
        cuenta.setMoneda(TipoMoneda.PESOS);
        
        when(cuentaDao.find(cuenta.getNumeroCuenta())).thenReturn(null);
        Cuenta resultado = cuentaDao.find(cuenta.getNumeroCuenta());
        assertNull(resultado);
        cuentaService.darDeAltaCuenta(cuenta, pepeRino.getDni());

        Cuenta cuenta2 = new Cuenta();
        cuenta2.setNumeroCuenta(987654321);
        cuenta2.setBalance(10000);
        cuenta2.setTipoCuenta(TipoCuenta.CUENTA_CORRIENTE);
        cuenta2.setMoneda(TipoMoneda.PESOS);

        doThrow(TipoCuentaAlreadyExistsException.class).when(clienteService).agregarCuenta(cuenta2, pepeRino.getDni());

        assertThrows(TipoCuentaAlreadyExistsException.class, () -> cuentaService.darDeAltaCuenta(cuenta2, pepeRino.getDni()));

        verify(clienteService, times(1)).agregarCuenta(cuenta2, pepeRino.getDni());
        verify(cuentaDao, times(1)).save(cuenta);
        verify(cuentaDao, times(2)).find(cuenta.getNumeroCuenta());
    }

    //Generar casos de test para darDeAltaCuenta
    //    1 - cuenta existente (DONE)
    //    2 - cuenta no soportada (DONE)
    //    3 - cliente ya tiene cuenta de ese tipo (DONE)
    //    4 - cuenta creada exitosamente (DONE)
}
