/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.server.handlers;

import chat.exceptions.InvalidOperationException;
import chat.models.Grupo;
import chat.models.UsuarioGrupo;
import chat.paquetes.models.Paquete;
import chat.paquetes.requests.AlterGrupoRequest;
import chat.paquetes.requests.GruposRequest;
import chat.paquetes.responses.GenericResponse;
import chat.server.database.GrupoConnector;
import chat.server.database.MensajeConnector;
import chat.server.database.MensajeVistoConnector;
import chat.server.database.UsuarioGrupoConnector;
import chat.server.vinculo.Vinculo;


/*

Aquí había un problema: esta clase no modifica grupos, sino personas en un grupo

*/

/**
 *
 * @author Maritza
 */
public class AlterGrupoHandler implements Handler {
    
    private final AlterGrupoRequest.Operacion operacion;
    //private final Grupo grup;
    
    private final UsuarioGrupo ug;

    // Te faltaba el throws
    public AlterGrupoHandler( AlterGrupoRequest request) throws InvalidOperationException {
         
        String op = request.getValue(AlterGrupoRequest.PARAM_OPERACION);
        if (op.equals(AlterGrupoRequest.Operacion.ADD.getName())) {
            operacion = AlterGrupoRequest.Operacion.ADD;
        } else if (op.equals(AlterGrupoRequest.Operacion.REMOVE.getName())) {
            operacion = AlterGrupoRequest.Operacion.REMOVE;
        } else {
            throw new InvalidOperationException("Operación inválida");
        }

        ug = new UsuarioGrupo();
        ug.setId_grupo(Integer.parseInt(request.getValue(AlterGrupoRequest.PARAM_GRUPO)));
        ug.setId_usuario(request.getValue(AlterGrupoRequest.PARAM_USUARIO));
        ug.setStatus(false);

    }
    

    @Override
    public Paquete run() {
        GrupoConnector connector = new GrupoConnector();
        MensajeConnector connme = new MensajeConnector();
        MensajeVistoConnector visto = new MensajeVistoConnector();
        UsuarioGrupoConnector usua = new UsuarioGrupoConnector();
        boolean correct = false;

        switch (operacion) {
            case ADD:
                correct = usua.add(ug);
                break;
            case REMOVE:
                // Eliminar integrante
                correct = usua.eliminar(ug);
                // Si hay menos de dos personas, eliminar grupo
                if(usua.getAllUsuarios(ug.getId_grupo()).size() < 2){
                    correct = visto.eliminar(ug.getId_grupo()) && correct;
                    correct = connme.eliminar(ug.getId_grupo()) && correct;
                    correct = usua.eliminarGrupo(ug.getId_grupo()) && correct;
                    correct = connector.eliminarGrupo(ug.getId_grupo()) && correct;
                }
                break;
        }

        if (correct) {
            return new GenericResponse(GenericResponse.Status.CORRECT);
        }
        return new GenericResponse(GenericResponse.Status.INCORRECT);

    }
    
}
