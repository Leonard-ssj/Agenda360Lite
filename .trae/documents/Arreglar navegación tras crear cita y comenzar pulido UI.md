## Problema
Al crear la cita, la navegación no ocurre porque el `navigate(...)` está ejecutándose dentro del árbol composable. Las acciones de navegación deben dispararse como efecto secundario (LaunchedEffect) y limpiar el back stack para evitar que el formulario quede encima.

## Cambios Propuestos (App)
1. AppointmentFormScreen
- Mover la navegación al dashboard dentro de `LaunchedEffect(s.success)`.
- Usar `navController.navigate(Routes.DASHBOARD) { popUpTo(Routes.DASHBOARD) { inclusive = false }; launchSingleTop = true }` para volver al dashboard y quitar el formulario del back stack.
- Opcional: añadir `vm.resetSuccess()` para evitar re-intentos de navegación si se recompone.

2. AppointmentFormViewModel
- Añadir `fun resetSuccess()` que ponga `success = false`.

## Pruebas
- Login → Nueva cita → seleccionar cliente/servicio/fecha/slot → Crear.
- Verificar snackbar “Cita creada” y regresar automático al dashboard.
- Confirmar que el botón Atrás ya no regresa al formulario recién creado (se limpió del back stack).

## Pulido de UI (siguiente paso inmediato)
- Unificar paddings y espaciado vertical; añadir `Divider` entre secciones.
- Ordenar botones de fecha en una fila; estilos consistentes.
- Estados vacíos con mensajes amigables y acciones de recarga.
- Mostrar `Página N` y deshabilitar navegación cuando no hay más; conservar meta.
- Mantener un único `LazyColumn` sin listas anidadas.

## Entregables
- Ajuste de navegación y back stack en formulario.
- Validación en emulador del flujo completo.
- Pulido básico de UI aplicado en formulario y listas.