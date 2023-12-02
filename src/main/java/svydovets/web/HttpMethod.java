package svydovets.web;

/**
 * The {@code HttpMethod} enumeration represents the standard HTTP methods used in web communication.
 * Each constant in this enumeration corresponds to a commonly used HTTP method, defining the
 * operations that can be performed on a specified resource.
 *
 * <p>The following HTTP methods are defined in this enumeration:
 * <ul>
 *     <li>{@code GET}: Used to retrieve data from a specified resource.</li>
 *     <li>{@code POST}: Used to submit data to be processed to a specified resource.</li>
 *     <li>{@code PUT}: Used to update a specified resource or create a new resource if it does not exist.</li>
 *     <li>{@code DELETE}: Used to request the removal of a specified resource.</li>
 *     <li>{@code PATCH}: Used to apply partial modifications to a resource.</li>
 * </ul>
 *
 * <p>Instances of this enumeration are typically used to specify the HTTP method when making
 * requests in web applications, providing a clear and standardized way to indicate the intended
 * operation on a given resource.
 *
 */
public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    PATCH
}
